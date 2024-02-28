package io.github.simonalexs.tools;

import org.apache.commons.lang3.StringUtils;

public class StringUtil {
    public static final String SPACE = " ";

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
}
