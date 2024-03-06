package io.github.simonalexs.tools.other;


import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import io.github.simonalexs.base.StaticVariables;
import org.apache.commons.lang3.tuple.Pair;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static io.github.simonalexs.tools.other.PrintUtil.JDBCUtil.*;

/**
 * 带有 时间、线程 代码位置 信息的print工具类
 */
public class PrintUtil {
    public static void println(Object... objs) {
        doPrint(objs, System.out::println);
    }

    public static void print(Object... objs) {
        doPrint(objs, System.out::print);
    }

    public static void flush() {
        System.out.flush();
    }

    private static void doPrint(Object[] objs, Consumer<String> printer) {
        String str;
        if (objs == null) {
            str = toStr(false, null);
        } else {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < objs.length; i++) {
                if (i != 0) {
                    builder.append(" ");
                }
                builder.append(toStr(Config.PRETTY_PRINT, objs[i]));
            }
            str = builder.toString();
        }
        String info = wrap(str);
        printer.accept(info);
    }

    public static void printResultSet(ResultSet resultSet) {
        printResultSet(resultSet, Integer.MAX_VALUE);
    }

    public static void printResultSet(ResultSet resultSet, int printDataNum) {
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
            String wrapInfo = wrap("");
            if (!wrapInfo.isEmpty()) {
                System.out.println(wrapInfo);
            }
            System.out.println(resultSetStr);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void printList(List<?>... objList) {
        printList("", objList);
    }

    public static void printList(String lineSeparatorBetweenParam, List<?>... objList) {
        printList("", lineSeparatorBetweenParam, "", objList);
    }

    public static void printList(String header, String lineSeparatorBetweenParam, String footer, List<?>... objList) {
        if (objList == null || objList.length == 0) {
            println();
            return;
        }
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

        String wrapInfo = wrap("");
        if (!wrapInfo.isEmpty()) {
            System.out.println(wrapInfo);
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
            wholeWidth += width + COLUMN_INTERVAL_WIDTH;
        }
        wholeWidth -= COLUMN_INTERVAL_WIDTH;
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
                stringBuilder.append(JDBCUtil.COLUMN_INTERVAL_STR);
            }
        }
        return stringBuilder.toString();
    }

    public static String wrap(String msg) {
        if (!Config.PRINT_TIME_INFO) {
            // 不添加额外信息
            return msg;
        }
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
        return toStr(false, value);
    }

    private static String toStr(boolean prettyFormat, Object value) {
        if (value == null) {
            return "null";
        }
        if (value instanceof String) {
            return value.toString();
        }
        List<JSONWriter.Feature> featureList = new ArrayList<>(Arrays.asList(JSONWriter.Feature.FieldBased,
                JSONWriter.Feature.WriteBigDecimalAsPlain,
                JSONWriter.Feature.WriteNulls,
                JSONWriter.Feature.WriteMapNullValue,
                JSONWriter.Feature.UnquoteFieldName
                ));
        if (prettyFormat) {
            featureList.add(JSONWriter.Feature.PrettyFormat);
        }
        JSONWriter.Feature[] features = new JSONWriter.Feature[featureList.size()];
        for (int i = 0; i < featureList.size(); i++) {
            features[i] = featureList.get(i);
        }
        return JSON.toJSONString(value, features);
    }

    public static class Config {
        /**
         * print时是否输出 “时间、线程” 信息
         */
        private static boolean PRINT_TIME_INFO = true;
        /**
         * print时是否美化输出格式（用于json格式输出对象）
         */
        private static boolean PRETTY_PRINT = false;

        public static boolean isPrintTimeInfo() {
            return PRINT_TIME_INFO;
        }

        public static void setPrintTimeInfo(boolean printTimeInfo) {
            PRINT_TIME_INFO = printTimeInfo;
        }

        public static boolean isPrettyPrint() {
            return PRETTY_PRINT;
        }

        public static void setPrettyPrint(boolean prettyPrint) {
            PRETTY_PRINT = prettyPrint;
        }
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

        protected static final int COLUMN_INTERVAL_WIDTH = 4;
        protected static final String COLUMN_INTERVAL_STR = repeat(" ", COLUMN_INTERVAL_WIDTH);
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
            return getWordCount(toStr(value));
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
