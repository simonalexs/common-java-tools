package io.github.simonalexs.handler;

import io.github.simonalexs.Starter;
import io.github.simonalexs.tools.ClassScannerUtil;
import io.github.simonalexs.tools.StringUtil;
import io.github.simonalexs.annotation.Func;
import io.github.simonalexs.annotation.Param;
import io.github.simonalexs.tools.other.PrintUtil;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.math.BigInteger;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class UtilHandler {
    public static final Map<String, List<FuncInfo>> MODULE_2_FUNCS_MAP = new LinkedHashMap<>();

    static {
        String basePackageName = Starter.class.getPackage().getName();
        List<Class<?>> classList = ClassScannerUtil.searchClasses(basePackageName);
        Map<String, List<FuncInfo>> map = new LinkedHashMap<>();
        for (Class<?> aClass : classList) {
            for (Method method : aClass.getDeclaredMethods()) {
                if (method.getAnnotation(Func.class) != null) {
                    FuncInfo funcInfo = new FuncInfo(method);
                    String module = funcInfo.module();
                    if (!map.containsKey(module)) {
                        map.put(module, new ArrayList<>());
                    }
                    map.get(module).add(funcInfo);
                }
            }
        }
        // 按名称排序
        map.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(e -> {
                    List<FuncInfo> sortedFuncInfos = e.getValue().stream()
                            .sorted(Comparator.comparing(FuncInfo::name))
                            .collect(Collectors.toList());
                    MODULE_2_FUNCS_MAP.put(e.getKey(), sortedFuncInfos);
                });
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
                paramInfo.parser = StringUtil.getParser(parameter.getType());
                this.params.add(paramInfo);
            }
        }

        public FuncInfo clone() {
            return new FuncInfo(func);
        }

        public String name() {
            return func.getName();
        }

        public String module() {
            String className = func.getDeclaringClass().getSimpleName();
            String classPrefix = className.replaceAll("(?i)util$", "");
            return StringUtil.toLowerCaseFirst(classPrefix);
        }

        public void show() {
            System.out.println();
            System.out.println("func info:");
            List<List<String>> funcContent = Arrays.asList(
                    Arrays.asList("module", "name", "desc"),
                    Arrays.asList(this.module(), this.name(), this.desc)
            );
            PrintUtil.println(funcContent);

            System.out.println("param info:");
            List<String> titles = Arrays.asList(
                    "order",
                    "paramName",
                    "currentValue",
                    "desc"
            );
            List<List<Object>> paramContent = new ArrayList<>();
            for (int i = 0; i < this.params.size(); i++) {
                UtilHandler.ParamInfo paramInfo = this.params.get(i);
                List<Object> paramDescription = Arrays.asList(
                        i + 1,
                        paramInfo.name,
                        paramInfo.currentValue,
                        paramInfo.tip
                );
                paramContent.add(paramDescription);
            }
            PrintUtil.println("=", "-", "=", titles, paramContent);
            printCurrentCommand();
            System.out.println();
        }

        private void printCurrentCommand() {
            StringBuilder command = new StringBuilder();
            command.append(this.module()).append(" ")
                    .append(this.name());
            for (UtilHandler.ParamInfo param : this.params) {
                command.append(" ").append(param.currentValue);
            }
            System.out.println("*************** current command is: " + command + " ******************");
        }

        public String run() {
            try {
                Object[] paramValues = new Object[this.params.size()];
                for (int i = 0; i < this.params.size(); i++) {
                    UtilHandler.ParamInfo param = this.params.get(i);
                    Object value = param.parser.apply(param.currentValue);
                    paramValues[i] = value;
                }
                Object invokedResult = this.func.invoke(null, paramValues);
                System.out.print("running success, ");
                Class<?> returnType = this.func.getReturnType();
                if (returnType == Void.class) {
                    System.out.println("no result.");
                } else {
                    System.out.println("result: " + invokedResult);
                }
                return "";
            } catch (Exception e) {
                return e.getClass().getName() + ": " + e.getMessage();
            }
        }
    }
}
