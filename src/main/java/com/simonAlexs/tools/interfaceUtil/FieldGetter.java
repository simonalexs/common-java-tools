package com.simonAlexs.tools.interfaceUtil;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ReflectUtil;
import lombok.SneakyThrows;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.function.Function;

public interface FieldGetter<T, R> extends Function<T, R>, Serializable {
    @SneakyThrows
    default String getFieldName() {
        Method method = ReflectUtil.getMethodByName(this.getClass(), "writeReplace");
        method.setAccessible(true);
        SerializedLambda serializedLambda = (SerializedLambda) method.invoke(this);
        String methodName = serializedLambda.getImplMethodName();
        if (methodName.startsWith("get")) {
            methodName = methodName.substring(3);
        } else if (methodName.startsWith("is")) {
            methodName = methodName.substring(2);
        }
        // 首字母变小写
        return CharSequenceUtil.lowerFirst(methodName);
    }
}

