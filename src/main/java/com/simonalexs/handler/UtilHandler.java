package com.simonalexs.handler;

import com.simonalexs.Starter;
import com.simonalexs.tools.ClassScannerUtil;
import com.simonalexs.tools.StringUtil;
import com.simonalexs.tools.annotation.Func;
import com.simonalexs.tools.annotation.Param;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.math.BigInteger;
import java.util.*;
import java.util.function.Function;

public class UtilHandler {
    public static final Map<String, List<FuncInfo>> MODULE_2_FUNCS_MAP = new LinkedHashMap<>();

    static {
        String basePackageName = Starter.class.getPackage().getName();
        List<Class<?>> classList = ClassScannerUtil.searchClasses(basePackageName);
        for (Class<?> aClass : classList) {
            for (Method method : aClass.getDeclaredMethods()) {
                if (method.getAnnotation(Func.class) != null) {
                    FuncInfo funcInfo = new FuncInfo(method);
                    String module = funcInfo.module();
                    if (!MODULE_2_FUNCS_MAP.containsKey(module)) {
                        MODULE_2_FUNCS_MAP.put(module, new ArrayList<>());
                    }
                    MODULE_2_FUNCS_MAP.get(module).add(funcInfo);
                }
            }
        }
    }

    public static Map<String, List<FuncInfo>> getModule2FuncsMap() {
        Map<String, List<FuncInfo>> copy = new LinkedHashMap<>();
        MODULE_2_FUNCS_MAP.forEach((model, funcs) -> {
            List<FuncInfo> funcInfoListCopy = new ArrayList<>();
            for (FuncInfo func : funcs) {
                funcInfoListCopy.add(func.clone());
            }
            copy.put(model, funcInfoListCopy);
        });
        return copy;
    }

    public static class ParamInfo {
        public String name;
        public String tip;
        public Function<String, Object> parser;
        public String currentValue;
    }

    public static class FuncInfo {
        /**
         * class反射拿到的method
         */
        public Method func;

        public String desc;

        public List<ParamInfo> params;

        public FuncInfo(Method method) {
            this.func = method;
            Func methodAnnotation = method.getAnnotation(Func.class);
            if (methodAnnotation.value().isEmpty()) {
                this.desc = StringUtil.humpToSpace(name());
            } else {
                this.desc = methodAnnotation.value();
            }
            this.params = new ArrayList<>();
            for (Parameter parameter : method.getParameters()) {
                Param parameterAnnotation = parameter.getAnnotation(Param.class);
                ParamInfo paramInfo = new ParamInfo();
                paramInfo.name = parameter.getName();
                if (parameterAnnotation == null || parameterAnnotation.tip().isEmpty()) {
                    paramInfo.tip = StringUtil.humpToSpace(parameter.getName());
                } else {
                    paramInfo.tip = parameterAnnotation.tip();
                }
                paramInfo.currentValue = parameterAnnotation == null ? "" : parameterAnnotation.value();
                paramInfo.parser = getParser(parameter.getType());
                this.params.add(paramInfo);
            }
        }

        public FuncInfo clone() {
            return new FuncInfo(func);
        }

        private Function<String, Object> getParser(Class<?> targetTypeClass) {
            if (Boolean.class.equals(targetTypeClass) || boolean.class.equals(targetTypeClass)) {
                return str -> {
                    if (str.equals("1")) {
                        return true;
                    }
                    if (str.equals("0")) {
                        return false;
                    }
                    return Boolean.valueOf(str);
                };
            } else if (Integer.class.equals(targetTypeClass) || int.class.equals(targetTypeClass)) {
                return Integer::valueOf;
            } else if (Double.class.equals(targetTypeClass) || double.class.equals(targetTypeClass)) {
                return Double::valueOf;
            } else if (Float.class.equals(targetTypeClass) || float.class.equals(targetTypeClass)) {
                return Float::valueOf;
            } else if (Long.class.equals(targetTypeClass) || long.class.equals(targetTypeClass)) {
                return Long::valueOf;
            } else if (Short.class.equals(targetTypeClass) || short.class.equals(targetTypeClass)) {
                return Short::valueOf;
            } else if (Byte.class.equals(targetTypeClass) || byte.class.equals(targetTypeClass)) {
                return Byte::valueOf;
            } else if (BigInteger.class.equals(targetTypeClass)) {
                return BigInteger::new;
            } else {
                return str -> str;
            }
        }

        public String name() {
            return func.getName();
        }

        public String module() {
            String className = func.getDeclaringClass().getSimpleName();
            String classPrefix = className.replaceAll("(?i)util$", "");
            return StringUtil.toLowerCaseFirst(classPrefix);
        }
    }
}
