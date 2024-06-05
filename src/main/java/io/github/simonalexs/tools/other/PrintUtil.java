package io.github.simonalexs.tools.other;


import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static io.github.simonalexs.tools.other.PrintUtil.JDBCUtil.*;

/**
 * 带有 时间、线程 代码位置 信息的print工具类
 */
public class PrintUtil {
    private static final Class<PrintUtil> PRINT_UTIL_CLASS = PrintUtil.class;
    private static final String LINE_SEPARATOR = System.lineSeparator();

    public static void println(Object... objs) {
        doPrint(objs, System.out::println);
    }

    public static void printlnPretty(Object... objs) {
        boolean ori = Config.isPrettyPrint();
        Config.setPrettyPrint(true);
        doPrint(objs, System.out::println);
        Config.setPrettyPrint(ori);
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
                String typeName = SQL_TYPES.get(metaData.getColumnType(i));
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
        if (objList == null) {
            println();
            return;
        }
        Pair<List<Integer>, String> columnWidthConfig = getColumnWidthConfig(objList);
        List<Integer> widthConfig = columnWidthConfig.getLeft();
        int wholeWidth = columnWidthConfig.getRight().length();
        if (wholeWidth == 0) {
            wholeWidth = 10;
        }

        StringBuilder builder = new StringBuilder();
        boolean hasPreData = false;
        // header
        if (header != null && !header.isEmpty()) {
            builder.append(repeat(header, wholeWidth));
            hasPreData = true;
        }
        // content
        for (int i = 0; i < objList.length; i++) {
            if (hasPreData) {
                builder.append(LINE_SEPARATOR);
            }
            hasPreData = true;
            List<?> list = objList[i];
            if (!list.isEmpty() && List.class.isAssignableFrom(list.get(0).getClass())) {
                // 参数中的元素为list，也就是二维数组
                for (Object rowObj : list) {
                    String rowStr = generateRowStr((List<?>) rowObj, widthConfig);
                    builder.append(rowStr);
                }
            } else {
                // 参数中的元素为obj，也就是一维数组
                String rowStr = generateRowStr(list, widthConfig);
                builder.append(rowStr);
            }
            // line separator
            if (i < objList.length - 1 && lineSeparatorBetweenParam != null && !lineSeparatorBetweenParam.isEmpty()) {
                builder.append(LINE_SEPARATOR)
                        .append(repeat(lineSeparatorBetweenParam, wholeWidth));
            }
        }
        // footer
        if (footer != null && !footer.isEmpty()) {
            if (hasPreData) {
                builder.append(LINE_SEPARATOR);
            }
            builder.append(repeat(footer, wholeWidth));
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
            if (!list.isEmpty() && List.class.isAssignableFrom(list.get(0).getClass())) {
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
        for (int i = 0; i < columnWidthConfig.size(); i++) {
            wholeWidth += columnWidthConfig.get(i);
            if (i != 0) {
                wholeWidth += COLUMN_INTERVAL_WIDTH;
            }
        }
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
        StackTraceElement outerElement = getOuterStackTraceElement(currentThread);
        String fullClassName = outerElement.getClassName();
        String simpleClassName = fullClassName.substring(fullClassName.lastIndexOf(".") + 1);
        return time + " [" + currentThread.getName() + "] "
                + simpleClassName + "." + outerElement.getMethodName() + "[" + outerElement.getLineNumber() + "] " + msg;
    }

    /**
     * 获取调用该方法的调用方
     * @param currentThread 当前线程
     * @return 该方法调用方的StackTraceElement
     */
    private static StackTraceElement getOuterStackTraceElement(Thread currentThread) {
        StackTraceElement[] stackTraceElements = currentThread.getStackTrace();
        StackTraceElement outerElement = null;
        for (StackTraceElement element : stackTraceElements) {
            if (!element.getClassName().equalsIgnoreCase(Thread.class.getName())
                    && !element.getClassName().equalsIgnoreCase(PRINT_UTIL_CLASS.getName())) {
                outerElement = element;
                break;
            }
        }
        if (outerElement == null) {
            throw new RuntimeException("please use PrintUtil in other class, not in class 'PrintUtil'");
        }
        return outerElement;
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
                JSONWriter.Feature.WriteEnumsUsingName
                ));
        if (prettyFormat) {
            featureList.add(JSONWriter.Feature.PrettyFormat);
            // TODO-high：研究如何利用ansi转义序列，美化json字符串的颜色。2024/03/12 10:27:48
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
            String lineSeparator = columnWidthPair.getRight() + LINE_SEPARATOR;

            stringBuilder.append(lineSeparator);
            List<Integer> columnWidthConfig = columnWidthPair.getLeft();
            if (colNameList != null) {
                // 列名
                stringBuilder.append(generateRowStr(colNameList, columnWidthConfig)).append(LINE_SEPARATOR);
            }
            // 列类型
            stringBuilder.append(generateRowStr(matadataList, columnWidthConfig)).append(LINE_SEPARATOR);
            stringBuilder.append(lineSeparator);

            for (List<Object> row : rows) {
                stringBuilder.append(generateRowStr(row, columnWidthConfig)).append(LINE_SEPARATOR);
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
            ArrayList<Integer> countList = new ArrayList<>();
            for (List<?> list : objList) {
                int columnCount;
                if (!list.isEmpty() && List.class.isAssignableFrom(list.get(0).getClass())) {
                    // 参数中的元素为list
                    columnCount = ((List<?>) list.get(0)).size();
                } else {
                    // 参数中的元素为obj
                    columnCount = list.size();
                }
                countList.add(columnCount);
            }
            countList.removeIf(t -> t == 0);
            if (countList.isEmpty()) {
                return 0;
            }
            if (countList.size() == 1) {
                return countList.get(0);
            }
            // 校验列数是否一致
            if (countList.stream().anyMatch(t -> !Objects.equals(t, countList.get(0)))) {
                throw new RuntimeException("参数中列数不一致，请检查各参数（单list与嵌套list）的列数是否一致");
            }
            return countList.get(0);
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


    private static final Map<Integer, String> SQL_TYPES = new HashMap<>();
    static {
        try {
            Field[] fields = Types.class.getDeclaredFields();
            for (Field field : fields) {
                if (field.getType() == int.class) {
                    SQL_TYPES.put((int) field.get(null), field.getName());
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
