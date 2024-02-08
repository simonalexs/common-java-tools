package com.simonAlexs.tools.debugUtil;

import com.simonAlexs.tools.base.baseStruct.ConsolePrintCellConfig;
import com.simonAlexs.tools.base.baseStruct.StopWatchInfo;
import com.simonAlexs.tools.base.common.ConsolePrintTable;

import java.text.NumberFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName: StopWatchExpand
 * @Description: 扩展StopWatch，显示ms和s，并尝试支持多任务同时进行
 * @Author: wcy
 * @Date: 2022/4/18 09:58
 * @Version: 1.0
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
        builder.addTitle("Task order", new ConsolePrintCellConfig(t -> String.format("%14s", t)));
        builder.addTitle("Task name", new ConsolePrintCellConfig(t -> String.format("%30s", t)));
        builder.addTitle("s", new ConsolePrintCellConfig(t -> String.format("%9s", t)));
        builder.addTitle("ms", new ConsolePrintCellConfig(t -> String.format("%12s", t)));
        builder.addTitle("ns", new ConsolePrintCellConfig(t -> String.format("%18s", t)));
        if (ENABLE_PRINT_CODE_LINE) {
            builder.addTitle("Task begin code", new ConsolePrintCellConfig(t -> String.format("%80s", t)));
            builder.addTitle("Task end code", new ConsolePrintCellConfig(t -> String.format("%80s", t)));
        }

        NumberFormat ddf2 = NumberFormat.getNumberInstance() ;
        ddf2.setMaximumFractionDigits(4);

        if (order == null) {
            for (int i = 0; i < stopWatchList.size(); i++) {
                StopWatchInfo stopWatchInfo = stopWatchList.get(i);
                ConsolePrintTable.Builder.RowDataBuilder rowDataBuilder = getRowDataBuilder(stopWatchInfo, builder,
                        i + 1, ddf2);
                builder.addRowData(rowDataBuilder.build());
            }
        } else {
            StopWatchInfo stopWatchInfo = stopWatchList.get(order - 1);
            ConsolePrintTable.Builder.RowDataBuilder rowDataBuilder = getRowDataBuilder(stopWatchInfo, builder, order, ddf2);
            builder.addRowData(rowDataBuilder.build());
        }
        return builder.build().prettyPrint();
    }

    private static ConsolePrintTable.Builder.RowDataBuilder getRowDataBuilder(StopWatchInfo stopWatchInfo,
                                                                              ConsolePrintTable.Builder builder,
                                                                              int order, NumberFormat ddf2) {
        StopWatch.TaskInfo task = stopWatchInfo.getStopWatch().getTaskInfo()[0];

        ConsolePrintTable.Builder.RowDataBuilder rowDataBuilder = builder.getRowDataBuilder();
        rowDataBuilder.addData("Task order", order);
        rowDataBuilder.addData("Task name", task.getTaskName());
        rowDataBuilder.addData("s", ddf2.format(task.getTimeSeconds()));
        rowDataBuilder.addData("ms", task.getTimeMillis());
        rowDataBuilder.addData("ns", task.getTimeNanos());
        if (ENABLE_PRINT_CODE_LINE) {
            rowDataBuilder.addData("Task begin code", stopWatchInfo.getStartLineInfo());
            rowDataBuilder.addData("Task end code", stopWatchInfo.getEndLineInfo());
        }
        return rowDataBuilder;
    }

    private static String getAverageConsumingStr(int avgSkipNum) {
        ConsolePrintTable.Builder builder = ConsolePrintTable.getInstance("Average time-consuming list").getBuilder();
        builder.addTitle("Task order", new ConsolePrintCellConfig(t -> String.format("%14s", t)));
        builder.addTitle("Task name", new ConsolePrintCellConfig(t -> String.format("%30s", t)));
        builder.addTitle("Task run times", new ConsolePrintCellConfig(t -> String.format("%14s", t)));
        builder.addTitle("s", new ConsolePrintCellConfig(t -> String.format("%9s", t)));
        builder.addTitle("ms", new ConsolePrintCellConfig(t -> String.format("%12s", t)));
        builder.addTitle("ns", new ConsolePrintCellConfig(t -> String.format("%18s", t)));
        if (ENABLE_PRINT_CODE_LINE) {
            builder.addTitle("Task begin code", new ConsolePrintCellConfig(t -> String.format("%80s", t)));
            builder.addTitle("Task end code", new ConsolePrintCellConfig(t -> String.format("%80s", t)));
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

            ConsolePrintTable.Builder.RowDataBuilder rowDataBuilder = builder.getRowDataBuilder();
            rowDataBuilder.addData("Task order", i + 1);
            rowDataBuilder.addData("Task name", taskName);
            rowDataBuilder.addData("Task run times", stopWatchInfoList.size());
            rowDataBuilder.addData("s", ddf2.format(secondAverage));
            rowDataBuilder.addData("ms", ddf1.format(millisAverage));
            rowDataBuilder.addData("ns", ddf0.format(nanoAverage));
            if (ENABLE_PRINT_CODE_LINE) {
                rowDataBuilder.addData("Task begin code", stopWatchInfoList.get(0).getStartLineInfo());
                rowDataBuilder.addData("Task end code", stopWatchInfoList.get(0).getEndLineInfo());
            }
            builder.addRowData(rowDataBuilder.build());
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

