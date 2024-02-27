package com.simonalexs.tools;

import java.util.Collections;

public class StringUtil {
    public static final String SPACE = " ";

    public static String toLowerCaseFirst(String str) {
        if (isNullOrEmpty(str)) {
            return str;
        }
        if (str.length() == 1) {
            return str.toLowerCase();
        }
        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }

    public static String toUpperCaseFirst(String str) {
        if (isNullOrEmpty(str)) {
            return str;
        }
        if (str.length() == 1) {
            return str.toUpperCase();
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty();
    }

    /**
     * 驼峰转空格
     */
    public static String humpToSpace(String str) {
        if (isNullOrEmpty(str)) {
            return str;
        }
        return humpToAny(str, " ");
    }

    /**
     * 驼峰转下划线
     */
    public static String humpToUnderLine(String str) {
        if (isNullOrEmpty(str)) {
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
                    && !isUpperChar(thisChar) && isUpperChar(str.charAt(i + 1))) {
                String nextLowerCase = String.valueOf(str.charAt(i + 1)).toLowerCase();
                builder.append(separator)
                        .append(nextLowerCase);
                i++;
            }
        }
        return builder.toString();
    }

    /**
     * 驼峰转下划线
     */
    public static boolean isUpperChar(char c) {
        return c >= 'A' && c <= 'Z';
    }

    public static String repeat(String str, int times) {
        return String.join("", Collections.nCopies(times, str));
    }

    public static String repeat(char str, int times) {
        return repeat(String.valueOf(str), times);
    }

    /**
     * <p>Left pad a String with a specified String.</p>
     *
     * <p>Pad to a size of {@code size}.</p>
     *
     * <pre>
     * StringUtils.leftPad(null, *, *)      = null
     * StringUtils.leftPad("", 3, "z")      = "zzz"
     * StringUtils.leftPad("bat", 3, "yz")  = "bat"
     * StringUtils.leftPad("bat", 5, "yz")  = "yzbat"
     * StringUtils.leftPad("bat", 8, "yz")  = "yzyzybat"
     * StringUtils.leftPad("bat", 1, "yz")  = "bat"
     * StringUtils.leftPad("bat", -1, "yz") = "bat"
     * StringUtils.leftPad("bat", 5, null)  = "  bat"
     * StringUtils.leftPad("bat", 5, "")    = "  bat"
     * </pre>
     *
     * @param str  the String to pad out, may be null
     * @param size  the size to pad to
     * @param padStr  the String to pad with, null or empty treated as single space
     * @return left padded String or original String if no padding is necessary,
     *  {@code null} if null String input
     */
    public static String leftPad(final String str, final int size, String padStr) {
        if (str == null) {
            return null;
        }
        if (isEmpty(padStr)) {
            padStr = SPACE;
        }
        final int padLen = padStr.length();
        final int strLen = str.length();
        final int pads = size - strLen;
        if (pads <= 0) {
            return str; // returns original String when possible
        }
        if (padLen == 1 && pads <= 8192) {
            return repeat(padStr.charAt(0), pads).concat(str);
        }

        if (pads == padLen) {
            return padStr.concat(str);
        } else if (pads < padLen) {
            return padStr.substring(0, pads).concat(str);
        } else {
            final char[] padding = new char[pads];
            final char[] padChars = padStr.toCharArray();
            for (int i = 0; i < pads; i++) {
                padding[i] = padChars[i % padLen];
            }
            return new String(padding).concat(str);
        }
    }

    public static boolean isEmpty(final CharSequence cs) {
        return cs == null || cs.length() == 0;
    }
}
