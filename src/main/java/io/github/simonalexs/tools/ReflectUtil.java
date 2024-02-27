package io.github.simonalexs.tools;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ReflectUtil {
    public static Method getMethodIgnoreCase(Class<?> oriClazz, String name) {
        Class<?> clazz = oriClazz;
        while (clazz != null) {
            Method[] declaredMethods = clazz.getDeclaredMethods();
            for (Method method : declaredMethods) {
                if (method.getName().equalsIgnoreCase(name)) {
                    return method;
                }
            }
            clazz = clazz.getSuperclass();
        }
        return null;
    }

    public static Field getFieldIgnoreCase(Class<?> oriClazz, String name) {
        Class<?> clazz = oriClazz;
        while (clazz != null) {
            Field[] declaredFields = clazz.getDeclaredFields();
            for (Field field : declaredFields) {
                if (field.getName().equalsIgnoreCase(name)) {
                    return field;
                }
            }
            clazz = clazz.getSuperclass();
        }
        return null;
    }
}
