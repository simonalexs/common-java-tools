package com.simonAlexs.tools.other;

import java.util.Collections;
import java.util.List;

public class PrintUtil {

    private static final String WHITE_STR = " ";

    public static void printRowData(List<Object> rowData, int widthConfig) {
        System.out.println(formatRowData(rowData, widthConfig));
    }

    public static String formatRowData(List<Object> rowData, int widthConfig) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < rowData.size(); i++) {
            String formatted = formatObj(rowData.get(i), widthConfig);
            if (i != 0) {
                builder.append("\t");
            }
            builder.append(formatted);
        }
        return builder.toString();
    }

    public static String formatObj(Object value, int widthConfig) {
        int width = getWidth(value);
        return repeat(WHITE_STR, widthConfig - width) + toStr(value);
    }

    private static int getWidth(Object value) {
        if (value == null) {
            return 4;
        }
        return getWordCount(value.toString());
    }
    private static int getWordCount(String s)
    {
        int length = 0;
        for(int i = 0; i < s.length(); i++) {
            int ascii = Character.codePointAt(s, i);
            if (ascii >= 0 && ascii <=255) {
                length++;
            } else {
                length += 2;
            }
        }
        return length;
    }

    private static String repeat(String str, int times) {
        return String.join("", Collections.nCopies(times, str));
    }

    private static String toStr(Object value) {
        String str = value == null ? "null" : value.toString();
        return str + repeat(" ", 4);
    }
}
