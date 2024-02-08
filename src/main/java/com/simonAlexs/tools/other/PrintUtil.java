package com.simonAlexs.tools.other;


import java.io.PrintStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/**
 * 带有 时间、线程 代码位置 信息的print工具类
 */
public class PrintUtil {
    public static <T extends ResultSet> void println(T resultSet,
                                                    Function<T, List<String>> colTypeNameFunc,
                                                    Function<T, List<String>> colNameFunc,
                                                    Function<T, List<List<Object>>> dataFunc) {
        JDBCUtil.printResultSet(resultSet, colTypeNameFunc, colNameFunc, dataFunc);
    }

    public static void println(String msg) {
        String info = wrap(toStr(msg));
        System.out.println(info);
    }

    public static void println(Integer msg) {
        println(toStr(msg));
    }

    public static void println(Double msg) {
        println(toStr(msg));
    }

    public static void println(Float msg) {
        println(toStr(msg));
    }

    public static void println(BigInteger msg) {
        println(toStr(msg));
    }

    public static void println(Short msg) {
        println(toStr(msg));
    }

    public static void println(Long msg) {
        println(toStr(msg));
    }

    public static void println(Byte msg) {
        println(toStr(msg));
    }

    public static void println(BigDecimal msg) {
        println(toStr(msg));
    }

    private static String wrap(String msg) {
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
        Thread currentThread = Thread.currentThread();
        // 获取调用该方法的调用方
        StackTraceElement element = currentThread.getStackTrace()[2];
        String fullClassName = element.getClassName();
        String simpleClassName = fullClassName.substring(fullClassName.lastIndexOf(".") + 1);
        return time + " 【" + currentThread.getName() + "】 "
                + simpleClassName + "." + element.getMethodName() + "[" + element.getLineNumber() + "] " + msg;
    }

    private static String toStr(Object value) {
        return value == null ? "null" : value.toString();
    }

    private static class JDBCUtil {
        /**
         * 打印 ResultSet
         */
        public static <T extends ResultSet> void printResultSet(T resultSet,
                                          Function<T, List<String>> colTypeNameFunc,
                                          Function<T, List<String>> colNameFunc,
                                          Function<T, List<List<Object>>> dataFunc) {
            println(colTypeNameFunc.apply(resultSet),
                    dataFunc.apply(resultSet),
                    colNameFunc.apply(resultSet));
        }

        private static void println(List<String> matadataList, List<List<Object>> rows, List<String> colNameList) {
            final PrintStream printStream = System.out;
            int columnCount = matadataList.size();
            ArrayList<Integer> columnWidthConfig = getColumnWidthConfig(matadataList, rows, colNameList, columnCount);

            int wholeWidth = 0;
            for (Integer width : columnWidthConfig) {
                wholeWidth += width + columnIntervalWidth;
            }
            wholeWidth -= columnIntervalWidth;
            String seperatorStr = repeat("-", wholeWidth);

            printStream.println(seperatorStr);
            if (colNameList != null) {
                // 列名
                for (int i = 0; i < columnCount; i++) {
                    printStream.print(format(columnWidthConfig.get(i), colNameList.get(i)));
                    if (i != columnCount - 1) {
                        printStream.print(columnIntervalStr);
                    }
                }
                printStream.println();
            }
            // 列类型
            for (int i = 0; i < columnCount; i++) {
                printStream.print(format(columnWidthConfig.get(i), matadataList.get(i)));
                if (i != columnCount - 1) {
                    printStream.print(columnIntervalStr);
                }
            }
            printStream.println();

            for (List<Object> row : rows) {
                for (int i = 0; i < columnCount; i++) {
                    printStream.print(format(columnWidthConfig.get(i), row.get(i)));
                    if (i != columnCount - 1) {
                        printStream.print(columnIntervalStr);
                    }
                }
                printStream.println();
            }
            printStream.println(seperatorStr);
            printStream.println();
        }

        public static String formatRowData(List<?> rowData) {
            int maxWidth = 0;
            for (Object rowDatum : rowData) {
                int width = getWidth(rowDatum);
                maxWidth = Math.max(maxWidth, width);
            }

            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < rowData.size(); i++) {
                String formatted = formatObj(rowData.get(i), maxWidth);
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


        private static final int columnIntervalWidth = 4;
        private static final String columnIntervalStr = repeat(" ", columnIntervalWidth);
        private static final String WHITE_STR = " ";

        private static ArrayList<Integer> getColumnWidthConfig(List<String> matadataList, List<List<Object>> rows, List<String> colNameList, int columnCount) {
            ArrayList<Integer> columnWidthConfig = new ArrayList<>(columnCount);
            for (int i = 0; i < columnCount; i++) {
                if (colNameList == null) {
                    int widthTypeName = getWidth(matadataList.get(i));
                    columnWidthConfig.add(i, widthTypeName);
                } else {
                    int widthName = getWidth(colNameList.get(i));
                    int widthTypeName = getWidth(matadataList.get(i));
                    columnWidthConfig.add(i, Math.max(widthName, widthTypeName));
                }
            }
            for (List<?> row : rows) {
                for (int j = 0; j < row.size(); j++) {
                    int width = getWidth(row.get(j));
                    if (columnWidthConfig.get(j) < width) {
                        columnWidthConfig.set(j, width);
                    }
                }
            }
            return columnWidthConfig;
        }


        private static int getWidth(Object value) {
            if (value == null) {
                return 4;
            }
            return getWordCount(value.toString());
        }
        private static String format(int widthConfig, Object value) {
            int width = getWidth(value);
            return repeat(WHITE_STR, widthConfig - width) + toStr(value);
        }

        private static String repeat(String str, int times) {
            return String.join("", Collections.nCopies(times, str));
        }

        private static String toStr(Object value) {
            String str = value == null ? "null" : value.toString();
            return str + repeat(" ", 4);
        }
        private static int getWordCount(String s) {
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
    }
}
