package io.github.simonalexs.tools.other;


import io.github.simonalexs.tools.base.StaticVariables;
import io.github.simonalexs.tools.base.tuple.Pair;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static io.github.simonalexs.tools.other.PrintUtil.JDBCUtil.*;

/**
 * 带有 时间、线程 代码位置 信息的print工具类
 */
public class PrintUtil {
    public static <T extends ResultSet> void println(T resultSet) {
        println(resultSet, Integer.MAX_VALUE);
    }

    public static <T extends ResultSet> void println(T resultSet, int printDataNum) {
        try {
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            List<String> colTypeNameList = new ArrayList<>();
            for (int i = 1; i <= columnCount; i++) {
                String typeName = StaticVariables.SQL_TYPES.get(metaData.getColumnType(i));
                colTypeNameList.add(typeName);
            }

            List<String> colNameList = new ArrayList<>();
            for (int i = 1; i <= columnCount; i++) {
                colNameList.add(metaData.getColumnLabel(i));
            }

            List<List<Object>> dataList = new ArrayList<>();
            int realPrintDataNum = Math.max(printDataNum, 0);
            int readiedNums = 0;
            while (readiedNums < realPrintDataNum && resultSet.next()) {
                List<Object> row = new ArrayList<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.add(resultSet.getObject(i));
                }
                dataList.add(row);
                readiedNums++;
            }
            String resultSetStr = generateResultSetStr(colTypeNameList, dataList, colNameList);
            System.out.println(resultSetStr);
            System.out.println();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void println(List<?>... objList) {
        println(null, objList);
    }

    public static void println(String lineSeparatorBetweenParam, List<?>... objList) {
        println(null, lineSeparatorBetweenParam, null, objList);
    }

    public static void println(String header, String lineSeparatorBetweenParam, String footer, List<?>... objList) {
        Pair<List<Integer>, String> columnWidthConfig = getColumnWidthConfig(objList);
        List<Integer> widthConfig = columnWidthConfig.getLeft();
        int wholeWidth = columnWidthConfig.getRight().length();

        StringBuilder builder = new StringBuilder();
        if (header != null && !header.isEmpty()) {
            builder.append(repeat(header, wholeWidth))
                    .append("\n");
        }
        for (int i = 0; i < objList.length; i++) {
            List<?> list = objList[i];
            if (List.class.isAssignableFrom(list.get(0).getClass())) {
                // 参数中的元素为list，也就是二维数组
                for (Object rowObj : list) {
                    String rowStr = generateRowStr((List<?>) rowObj, widthConfig);
                    builder.append(rowStr)
                            .append("\n");
                }
            } else {
                // 参数中的元素为obj，也就是一维数组
                String rowStr = generateRowStr(list, widthConfig);
                builder.append(rowStr)
                        .append("\n");
            }
            if (i < objList.length - 1 && lineSeparatorBetweenParam != null && !lineSeparatorBetweenParam.isEmpty()) {
                builder.append(repeat(lineSeparatorBetweenParam, wholeWidth))
                        .append("\n");
            }
        }
        if (footer != null && !footer.isEmpty()) {
            builder.append(repeat(footer, wholeWidth))
                    .append("\n");
        }
        System.out.println(builder);
    }

    public static Pair<List<Integer>, String> getColumnWidthConfig(List<?>... objList) {
        int realColumnCount = getRealColumnCount(objList);
        List<Integer> columnWidthConfig = IntStream.range(0, realColumnCount).boxed().map(t -> 0).collect(Collectors.toList());
        for (List<?> list : objList) {
            if (List.class.isAssignableFrom(list.get(0).getClass())) {
                // 参数中的元素为list
                for (Object rowObj : list) {
                    scanRowColumnWidth((List<?>) rowObj, columnWidthConfig);
                }
            } else {
                // 参数中的元素为obj
                scanRowColumnWidth(list, columnWidthConfig);
            }
        }
        int wholeWidth = 0;
        for (Integer width : columnWidthConfig) {
            wholeWidth += width + columnIntervalWidth;
        }
        wholeWidth -= columnIntervalWidth;
        String seperatorStr = repeat("-", wholeWidth);
        return Pair.of(columnWidthConfig, seperatorStr);
    }

    public static String generateRowStr(List<?> row, List<Integer> columnWidthConfig) {
        // TODO-high：可以增加参数，控制【行首缩进字符数】。2024/02/27 09:08:35
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < row.size(); i++) {
            String formatted = formatObj(row.get(i), columnWidthConfig.get(i));
            stringBuilder.append(formatted);
            if (i != row.size() - 1) {
                stringBuilder.append(JDBCUtil.columnIntervalStr);
            }
        }
        return stringBuilder.toString();
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

    protected static class JDBCUtil {

        static String generateResultSetStr(List<String> matadataList, List<List<Object>> rows, List<String> colNameList) {
            StringBuilder stringBuilder = new StringBuilder();
            Pair<List<Integer>, String> columnWidthPair = getColumnWidthConfig(matadataList, rows, colNameList);
            String lineSeparator = columnWidthPair.getRight() + "\n";

            stringBuilder.append(lineSeparator);
            List<Integer> columnWidthConfig = columnWidthPair.getLeft();
            if (colNameList != null) {
                // 列名
                stringBuilder.append(generateRowStr(colNameList, columnWidthConfig)).append("\n");
            }
            // 列类型
            stringBuilder.append(generateRowStr(matadataList, columnWidthConfig)).append("\n");
            stringBuilder.append(lineSeparator);

            for (List<Object> row : rows) {
                stringBuilder.append(generateRowStr(row, columnWidthConfig)).append("\n");
            }
            stringBuilder.append(lineSeparator);

            return stringBuilder.toString();
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

        static String formatObj(Object value, int widthConfig) {
            int width = getWidth(value);
            // TODO-high：可以增加参数，控制【居中，左对齐，右对齐】。2024/02/27 09:08:35
            return repeat(WHITE_STR, widthConfig - width) + toStr(value);
        }

        protected static final int columnIntervalWidth = 4;
        protected static final String columnIntervalStr = repeat(" ", columnIntervalWidth);
        protected static final String WHITE_STR = " ";

        protected static void scanRowColumnWidth(List<?> row, List<Integer> columnWidthConfig) {
            for (int i = 0; i < row.size(); i++) {
                Object value = row.get(i);
                int thisWidth = getWidth(value);
                Integer preWidth = columnWidthConfig.get(i);
                int max = Math.max(thisWidth, preWidth);
                columnWidthConfig.set(i, max);
            }
        }

        protected static int getRealColumnCount(List<?>[] objList) {
            int realColumnCount = 0;
            for (List<?> list : objList) {
                if (!list.isEmpty() && List.class.isAssignableFrom(list.get(0).getClass())) {
                    // 参数中的元素为list
                    int columnCount = ((List<?>) list.get(0)).size();
                    if (realColumnCount != 0 && realColumnCount != columnCount) {
                        throw new RuntimeException("参数中列数不一致，请检查各参数（单list与嵌套list）的列数是否一致");
                    }
                    realColumnCount = columnCount;
                } else {
                    // 参数中的元素为obj
                    int columnCount = list.size();
                    if (realColumnCount != 0 && realColumnCount != columnCount) {
                        throw new RuntimeException("参数中列数不一致，请检查各参数（单list与嵌套list）的列数是否一致");
                    }
                    realColumnCount = columnCount;
                }
            }
            return realColumnCount;
        }


        protected static int getWidth(Object value) {
            if (value == null) {
                return 4;
            }
            return getWordCount(value.toString());
        }

        protected static String repeat(String str, int times) {
            return String.join("", Collections.nCopies(times, str));
        }

        protected static int getWordCount(String s) {
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
