package io.github.simonalexs.tools.debug;

import io.github.simonalexs.base.common.ConsolePrintTable;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 扩展StopWatch，显示ms和s
 */
public class StopWatchSingle {
    private static StopWatch singleStopWatch = new StopWatch();
    private static final List<String> startLineInfos = new ArrayList<>();
    private static final List<String> endLineInfos = new ArrayList<>();

    public static String start(String taskName){
        if (singleStopWatch.isRunning()) {
            stop();
        }
        singleStopWatch.start(taskName);
        startLineInfos.add(getCurrentCodeLineInfo());
        return "[任务：" + taskName + "]监测运行时间开始......";
    }

    public static String start(){
        return start("");
    }

    public static void stop(){
        if (singleStopWatch.isRunning()) {
            singleStopWatch.stop();
            endLineInfos.add(getCurrentCodeLineInfo());
        }
    }

    public static void clear(){
        singleStopWatch = new StopWatch();
        startLineInfos.clear();
        endLineInfos.clear();
    }

    public static void print() {
        // 获取运行的毫秒数与秒数
        double totalTimeNanos = singleStopWatch.getTotalTimeNanos();
        long totalTimeMillis = singleStopWatch.getTotalTimeMillis();
        double totalTimeSeconds = singleStopWatch.getTotalTimeSeconds();
        // 编写总结
        String shortSummary = "StopWatch '" + singleStopWatch.getId() + "': running time [ " + String.format("%9s", totalTimeMillis) + "ms / " + String.format("%9.3f", totalTimeSeconds) + "s ]";

        ConsolePrintTable.Builder builder = ConsolePrintTable.getInstance().getBuilder();
        builder.addTitle(Arrays.asList(
                "Task name",
                "s",
                "ms",
                "ns",
                "%",
                "Task begin code",
                "Task end code"
        ));

        NumberFormat ddf1 = NumberFormat.getNumberInstance() ;
        ddf1.setMaximumFractionDigits(2);
        NumberFormat ddf2 = NumberFormat.getNumberInstance() ;
        ddf2.setMaximumFractionDigits(4);
        for (int i = 0; i < singleStopWatch.getTaskInfo().length; i++) {
            StopWatch.TaskInfo task = singleStopWatch.getTaskInfo()[i];
            List<Object> rowData = Arrays.asList(
                    task.getTaskName(),
                    ddf2.format(task.getTimeSeconds()),
                    task.getTimeMillis(),
                    task.getTimeNanos(),
                    ddf1.format((double) task.getTimeNanos() / totalTimeNanos * 100),
                    startLineInfos.get(i),
                    endLineInfos.get(i)
            );
            builder.addRowData(rowData);
        }
        String prettyPrintStr = builder.build().prettyPrint();
        System.out.println(prettyPrintStr);
    }

    private static String getCurrentCodeLineInfo(){
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        for (StackTraceElement stackTraceElement : stackTraceElements) {
            if (!stackTraceElement.getClassName().endsWith(StopWatchSingle.class.getName())
                && !stackTraceElement.getClassName().endsWith(Thread.class.getName())) {
                return stackTraceElement.getClassName() + "#" + stackTraceElement.getMethodName() + "(" + stackTraceElement.getLineNumber() + ")";
            }
        }
        return "not find";
    }
}
