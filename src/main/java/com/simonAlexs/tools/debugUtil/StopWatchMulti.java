package com.simonAlexs.tools.debugUtil;

import com.simonAlexs.tools.base.baseStruct.ConsolePrintCellConfig;
import com.simonAlexs.tools.base.baseStruct.StopWatchInfo;
import com.simonAlexs.tools.base.common.ConsolePrintTable;
import org.springframework.util.StopWatch;

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
    private static List<StopWatchInfo> stopWatchList = new ArrayList<>();
    private static List<String> taskNameList = new ArrayList<>();

    public static String start(String taskName){
        return start(taskName, stopWatchList.size() + 1);
    }

    public static String start(String taskName, int order){
        StopWatchInfo stopWatchInfo = new StopWatchInfo();
        stopWatchInfo.setTaskName(taskName);
        stopWatchInfo.setStartLineInfo(getCurrentCodeLineInfo());

        stopWatchInfo.getStopWatch().start(taskName);
        stopWatchList.add(stopWatchInfo);
        if (!taskNameList.contains(taskName)) {
            taskNameList.add(taskName);
        }
        return "[任务" + String.format("%3s", order) + "：" + taskName + "]监测运行时间开始......";
    }

    public static void stop(String taskName){
        for (int i = 0; i < stopWatchList.size(); i++) {
            if (stopWatchList.get(i).getTaskName().equals(taskName) && stopWatchList.get(i).getStopWatch().isRunning()) {
                stop(i + 1);
                break;
            }
        }
    }

    public static void stop(int order){
        stopWatchList.get(order - 1).getStopWatch().stop();
        stopWatchList.get(order - 1).setEndLineInfo(getCurrentCodeLineInfo());
    }

    public static String print() {
        String commonConsumingStr = getCommonConsumingStr();
        String averageConsumingStr = getAverageConsumingStr();
        return commonConsumingStr + "\n" + averageConsumingStr;
    }

    private static String getCommonConsumingStr() {
        ConsolePrintTable.Builder builder = ConsolePrintTable.getInstance("Time-consuming list").getBuilder();
        builder.addTitle("ns", new ConsolePrintCellConfig(t -> String.format("%18s", t)));
        builder.addTitle("ms", new ConsolePrintCellConfig(t -> String.format("%12s", t)));
        builder.addTitle("s", new ConsolePrintCellConfig(t -> String.format("%9s", t)));
        builder.addTitle("Task name", new ConsolePrintCellConfig(t -> String.format("%30s", t)));
        builder.addTitle("Task order", new ConsolePrintCellConfig(t -> String.format("%14s", t)));
        builder.addTitle("Task begin code", new ConsolePrintCellConfig(t -> String.format("%80s", t)));
        builder.addTitle("Task end code", new ConsolePrintCellConfig(t -> String.format("%80s", t)));

        NumberFormat ddf2 = NumberFormat.getNumberInstance() ;
        ddf2.setMaximumFractionDigits(4);

        for (int i = 0; i < stopWatchList.size(); i++) {
            StopWatchInfo stopWatchInfo = stopWatchList.get(i);
            StopWatch.TaskInfo task = stopWatchInfo.getStopWatch().getTaskInfo()[0];

            ConsolePrintTable.Builder.RowDataBuilder rowDataBuilder = builder.getRowDataBuilder();
            rowDataBuilder.addData("ns", task.getTimeNanos());
            rowDataBuilder.addData("ms", task.getTimeMillis());
            rowDataBuilder.addData("s", ddf2.format(task.getTimeSeconds()));
            rowDataBuilder.addData("Task name", task.getTaskName());
            rowDataBuilder.addData("Task order", i + 1);
            rowDataBuilder.addData("Task begin code", stopWatchInfo.getStartLineInfo());
            rowDataBuilder.addData("Task end code", stopWatchInfo.getEndLineInfo());
            builder.addRowData(rowDataBuilder.build());
        }
        return builder.build().prettyPrint();
    }

    private static String getAverageConsumingStr() {
        ConsolePrintTable.Builder builder = ConsolePrintTable.getInstance("Average time-consuming list").getBuilder();
        builder.addTitle("ns", new ConsolePrintCellConfig(t -> String.format("%18s", t)));
        builder.addTitle("ms", new ConsolePrintCellConfig(t -> String.format("%12s", t)));
        builder.addTitle("s", new ConsolePrintCellConfig(t -> String.format("%9s", t)));
        builder.addTitle("Task name", new ConsolePrintCellConfig(t -> String.format("%30s", t)));
        builder.addTitle("Task order", new ConsolePrintCellConfig(t -> String.format("%14s", t)));
        builder.addTitle("Task run times", new ConsolePrintCellConfig(t -> String.format("%14s", t)));
        builder.addTitle("Task begin code", new ConsolePrintCellConfig(t -> String.format("%80s", t)));
        builder.addTitle("Task end code", new ConsolePrintCellConfig(t -> String.format("%80s", t)));

        NumberFormat ddf0 = NumberFormat.getNumberInstance() ;
        ddf0.setMaximumFractionDigits(0);
        NumberFormat ddf1 = NumberFormat.getNumberInstance() ;
        ddf1.setMaximumFractionDigits(2);
        NumberFormat ddf2 = NumberFormat.getNumberInstance() ;
        ddf2.setMaximumFractionDigits(4);


        Map<String, List<StopWatchInfo>> collect = stopWatchList.stream().collect(Collectors.groupingBy(StopWatchInfo::getTaskName));

        for (int i = 0; i < taskNameList.size(); i++) {
            String taskName = taskNameList.get(i);
            List<StopWatchInfo> stopWatchInfoList = collect.get(taskName);
            Double nanoAverage = stopWatchInfoList.stream().collect(Collectors.averagingLong(t -> t.getStopWatch().getTaskInfo()[0].getTimeNanos()));
            Double millisAverage = stopWatchInfoList.stream().collect(Collectors.averagingLong(t -> t.getStopWatch().getTaskInfo()[0].getTimeMillis()));
            Double secondAverage = stopWatchInfoList.stream().collect(Collectors.averagingDouble(t -> t.getStopWatch().getTaskInfo()[0].getTimeSeconds()));

            ConsolePrintTable.Builder.RowDataBuilder rowDataBuilder = builder.getRowDataBuilder();
            rowDataBuilder.addData("ns", ddf0.format(nanoAverage));
            rowDataBuilder.addData("ms", ddf1.format(millisAverage));
            rowDataBuilder.addData("s", ddf2.format(secondAverage));
            rowDataBuilder.addData("Task name", taskName);
            rowDataBuilder.addData("Task order", i + 1);
            rowDataBuilder.addData("Task run times", stopWatchInfoList.size());
            rowDataBuilder.addData("Task begin code", stopWatchInfoList.get(0).getStartLineInfo());
            rowDataBuilder.addData("Task end code", stopWatchInfoList.get(0).getEndLineInfo());
            builder.addRowData(rowDataBuilder.build());
        }

        return builder.build().prettyPrint();
    }

    private static String getCurrentCodeLineInfo(){
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
}

