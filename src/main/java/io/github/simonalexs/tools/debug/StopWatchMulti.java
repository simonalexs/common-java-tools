package io.github.simonalexs.tools.debug;

import io.github.simonalexs.base.baseStruct.StopWatchInfo;
import io.github.simonalexs.base.common.ConsolePrintTable;

import java.text.NumberFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 扩展StopWatch，显示ms和s，并尝试支持多任务同时进行
 */
public class StopWatchMulti {
    public static boolean ENABLE_PRINT_CODE_LINE = false;

    private static List<StopWatchInfo> stopWatchList = new ArrayList<>();
    private static List<String> taskNameList = new ArrayList<>();

    public static void start(String taskName){
        StopWatchInfo stopWatchInfo = new StopWatchInfo();
        stopWatchInfo.setTaskName(taskName);
        stopWatchInfo.setStartLineInfo(getCurrentCodeLineInfo());

        stopWatchInfo.getStopWatch().start(taskName);
        stopWatchList.add(stopWatchInfo);
        if (!taskNameList.contains(taskName)) {
            taskNameList.add(taskName);
        }
    }

    public static void stop(String taskName) {
        for (int i = 0; i < stopWatchList.size(); i++) {
            if (stopWatchList.get(i).getTaskName().equals(taskName) && stopWatchList.get(i).getStopWatch().isRunning()) {
                stop(i + 1);
                return;
            }
        }
        throw new RuntimeException("task name 【" + taskName + "】 not exists or task name 【" + taskName + "】 is not running");
    }

    public static void stopAndPrint(String taskName) {
        for (int i = 0; i < stopWatchList.size(); i++) {
            if (stopWatchList.get(i).getTaskName().equals(taskName) && stopWatchList.get(i).getStopWatch().isRunning()) {
                stopAndPrint(i + 1);
                return;
            }
        }
        throw new RuntimeException("task name 【" + taskName + "】 not exists or task name 【" + taskName + "】 is not running");
    }

    public static void print() {
        String str = generateResultStr(0);
        System.out.println(str);
    }

    public static void print(int avgSkipNum) {
        String str = generateResultStr(avgSkipNum);
        System.out.println(str);
        if (avgSkipNum > 0) {
            System.out.println("每个任务统计平均耗时的时候，均已忽略前【" + avgSkipNum + "】条耗时信息");
        }
    }

    public static void clear() {
        stopWatchList.clear();
        taskNameList.clear();
    }

    //region private方法
    private static void stop(int order) {
        StopWatchInfo stopWatchInfo = stopWatchList.get(order - 1);
        if (!stopWatchInfo.getStopWatch().isRunning()) {
            throw new RuntimeException("task 【" + order + "】 is not running");
        }
        stopWatchInfo.getStopWatch().stop();
        stopWatchInfo.setEndLineInfo(getCurrentCodeLineInfo());
    }

    private static void stopAndPrint(int order) {
        stop(order);

        String commonConsumingStr = getCommonConsumingStr(order);
        System.out.println(commonConsumingStr);
    }

    private static String generateResultStr(int avgSkipNum) {
        for (StopWatchInfo stopWatchInfo : stopWatchList) {
            if (stopWatchInfo.getStopWatch().isRunning()) {
                throw new RuntimeException("task name 【" + stopWatchInfo.getTaskName() + "】 is still running, please " +
                        "stop all tasks before print.");
            }
        }

        String commonConsumingStr = getCommonConsumingStr(null);
        String averageConsumingStr = getAverageConsumingStr(avgSkipNum);
        return commonConsumingStr + "\n" + averageConsumingStr;
    }

    private static String getCommonConsumingStr(Integer order) {
        ConsolePrintTable.Builder builder = ConsolePrintTable.getInstance("Time-consuming list").getBuilder();
        builder.addTitle(Arrays.asList(
                "Task order",
                "Task name",
                "s",
                "ms",
                "ns"
                ));
        if (ENABLE_PRINT_CODE_LINE) {
            builder.addTitle("Task begin code");
            builder.addTitle("Task end code");
        }

        NumberFormat ddf2 = NumberFormat.getNumberInstance() ;
        ddf2.setMaximumFractionDigits(4);

        if (order == null) {
            for (int i = 0; i < stopWatchList.size(); i++) {
                StopWatchInfo stopWatchInfo = stopWatchList.get(i);
                List<Object> rowData = getRowData(stopWatchInfo, builder, i + 1, ddf2);
                builder.addRowData(rowData);
            }
        } else {
            StopWatchInfo stopWatchInfo = stopWatchList.get(order - 1);
            List<Object> rowData = getRowData(stopWatchInfo, builder, order, ddf2);
            builder.addRowData(rowData);
        }
        return builder.build().prettyPrint();
    }

    private static List<Object> getRowData(StopWatchInfo stopWatchInfo,
                                           ConsolePrintTable.Builder builder,
                                           int order, NumberFormat ddf2) {
        StopWatch.TaskInfo task = stopWatchInfo.getStopWatch().getTaskInfo()[0];
        List<Object> rowData = Arrays.asList(
                order,
                task.getTaskName(),
                ddf2.format(task.getTimeSeconds()),
                task.getTimeMillis(),
                task.getTimeNanos()
        );
        if (ENABLE_PRINT_CODE_LINE) {
            rowData.add(stopWatchInfo.getStartLineInfo());
            rowData.add(stopWatchInfo.getEndLineInfo());
        }
        return rowData;
    }

    private static String getAverageConsumingStr(int avgSkipNum) {
        ConsolePrintTable.Builder builder = ConsolePrintTable.getInstance("Average time-consuming list").getBuilder();
        builder.addTitle(Arrays.asList(
                "Task order",
                "Task name",
                "s",
                "ms",
                "ns"
        ));
        if (ENABLE_PRINT_CODE_LINE) {
            builder.addTitle("Task begin code");
            builder.addTitle("Task end code");
        }

        NumberFormat ddf0 = NumberFormat.getNumberInstance() ;
        ddf0.setMaximumFractionDigits(0);
        NumberFormat ddf1 = NumberFormat.getNumberInstance() ;
        ddf1.setMaximumFractionDigits(2);
        NumberFormat ddf2 = NumberFormat.getNumberInstance() ;
        ddf2.setMaximumFractionDigits(4);

        Map<String, List<StopWatchInfo>> collect = stopWatchList.stream()
                        .collect(Collectors.groupingBy(StopWatchInfo::getTaskName, LinkedHashMap::new, Collectors.toList()));

        for (int i = 0; i < taskNameList.size(); i++) {
            String taskName = taskNameList.get(i);
            List<StopWatchInfo> oriStopWatchInfoList = collect.get(taskName);
            List<StopWatchInfo> stopWatchInfoList = oriStopWatchInfoList.stream()
                            .skip(avgSkipNum).collect(Collectors.toList());
            if (stopWatchInfoList.isEmpty()) {
                continue;
            }
            Double nanoAverage = stopWatchInfoList.stream().collect(Collectors.averagingLong(t -> t.getStopWatch().getTaskInfo()[0].getTimeNanos()));
            Double millisAverage = stopWatchInfoList.stream().collect(Collectors.averagingLong(t -> t.getStopWatch().getTaskInfo()[0].getTimeMillis()));
            Double secondAverage = stopWatchInfoList.stream().collect(Collectors.averagingDouble(t -> t.getStopWatch().getTaskInfo()[0].getTimeSeconds()));

            List<Object> rowData = Arrays.asList(
                    i + 1,
                    taskName,
                    stopWatchInfoList.size(),
                    ddf2.format(secondAverage),
                    ddf1.format(millisAverage),
                    ddf0.format(nanoAverage)
            );
            if (ENABLE_PRINT_CODE_LINE) {
                rowData.add(stopWatchInfoList.get(0).getStartLineInfo());
                rowData.add(stopWatchInfoList.get(0).getEndLineInfo());
            }
            builder.addRowData(rowData);
        }
        return builder.build().prettyPrint();
    }

    private static String getCurrentCodeLineInfo() {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        boolean isPassCurrentClass = false;
        for (StackTraceElement stackTraceElement : stackTraceElements) {
            boolean isEndsWithCurrentClass = stackTraceElement.getClassName().endsWith(StopWatchMulti.class.getName());
            if (isEndsWithCurrentClass) {
                if (!isPassCurrentClass) {
                    isPassCurrentClass = true;
                    continue;
                }
            } else {
                if (isPassCurrentClass) {
                    return stackTraceElement.getClassName() + "#" + stackTraceElement.getMethodName() + "(" + stackTraceElement.getLineNumber() + ")";
                }
            }
        }
        return "not find";
    }
    //endregion
}

