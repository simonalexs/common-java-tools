package io.github.simonalexs.tools;

import org.apache.commons.lang3.StringUtils;

import java.math.BigInteger;
import java.util.function.Function;

public class StringUtil {
    public static String toLowerCaseFirst(String str) {
        if (StringUtils.isEmpty(str)) {
            return str;
        }
        if (str.length() == 1) {
            return str.toLowerCase();
        }
        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }

    public static String toUpperCaseFirst(String str) {
        if (StringUtils.isEmpty(str)) {
            return str;
        }
        if (str.length() == 1) {
            return str.toUpperCase();
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    /**
     * 驼峰转空格
     * @param str 字符串
     * @return 转化后的
     */
    public static String humpToSpace(String str) {
        if (StringUtils.isEmpty(str)) {
            return str;
        }
        return humpToAny(str, " ");
    }

    /**
     * 驼峰转下划线
     * @param str 字符串
     * @return 转化后的
     */
    public static String humpToUnderLine(String str) {
        if (StringUtils.isEmpty(str)) {
            return str;
        }
        return humpToAny(str, "_");
    }

    private static String humpToAny(String str, String separator) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char thisChar = str.charAt(i);
            builder.append(thisChar);
            if (i < str.length() - 1
                    && !isUpperCase(thisChar) && isUpperCase(str.charAt(i + 1))) {
                String nextLowerCase = String.valueOf(str.charAt(i + 1)).toLowerCase();
                builder.append(separator)
                        .append(nextLowerCase);
                i++;
            }
        }
        return builder.toString();
    }

    /**
     * 是否是大写字符
     * @param c char
     * @return isUpperChar
     */
    public static boolean isUpperCase(char c) {
        return c >= 'A' && c <= 'Z';
    }

    public static<T> T parse(String str, Class<T> clazz) {
        if (str == null || str.isEmpty() && clazz != String.class) {
            return null;
        }
        return clazz.cast(getParser(clazz).apply(str));
    }

    public static Function<String, Object> getParser(Class<?> targetTypeClass) {
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
}
