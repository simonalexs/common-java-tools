package simonAlexs.tools.debugUtil;

import simonAlexs.tools.common.ConsolePrintTable;
import simonAlexs.tools.baseStruct.ConsolePrintCellConfig;
import org.springframework.util.StopWatch;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: StopWatchExpand
 * @Description: 扩展StopWatch，显示ms和s
 * @Author: wcy
 * @Date: 2022/4/18 09:58
 * @Version: 1.0
 */
public class StopWatchSingle {
    private static StopWatch singleStopWatch = new StopWatch();
    private static List<String> startLineInfos = new ArrayList<>();
    private static List<String> endLineInfos = new ArrayList<>();


    public static String start(String taskName){
        singleStopWatch.start(taskName);
        startLineInfos.add(getCurrentCodeLineInfo());
        return "[任务：" + taskName + "]监测运行时间开始......";
    }

    public static void stop(){
        singleStopWatch.stop();
        endLineInfos.add(getCurrentCodeLineInfo());
    }

    public static String print() {
        // 获取运行的毫秒数与秒数
        double totalTimeNanos = singleStopWatch.getTotalTimeNanos();
        long totalTimeMillis = singleStopWatch.getTotalTimeMillis();
        double totalTimeSeconds = singleStopWatch.getTotalTimeSeconds();
        // 编写总结
        String shortSummary = "StopWatch '" + singleStopWatch.getId() + "': running time [ " + String.format("%9s", totalTimeMillis) + "ms / " + String.format("%9.3f", totalTimeSeconds) + "s ]";

        ConsolePrintTable.Builder builder = ConsolePrintTable.getInstance().getBuilder();
        builder.addTitle("ns", new ConsolePrintCellConfig(t -> String.format("%18s", t)));
        builder.addTitle("ms", new ConsolePrintCellConfig(t -> String.format("%12s", t)));
        builder.addTitle("s", new ConsolePrintCellConfig(t -> String.format("%9s", t)));
        builder.addTitle("%", new ConsolePrintCellConfig(t -> String.format("%9s", t)));
        builder.addTitle("Task name", new ConsolePrintCellConfig(t -> String.format("%30s", t)));
        builder.addTitle("Task begin code", new ConsolePrintCellConfig(t -> String.format("%80s", t)));
        builder.addTitle("Task end code", new ConsolePrintCellConfig(t -> String.format("%80s", t)));

        NumberFormat ddf1 = NumberFormat.getNumberInstance() ;
        ddf1.setMaximumFractionDigits(2);
        NumberFormat ddf2 = NumberFormat.getNumberInstance() ;
        ddf2.setMaximumFractionDigits(4);
        for (int i = 0; i < singleStopWatch.getTaskInfo().length; i++) {
            StopWatch.TaskInfo task = singleStopWatch.getTaskInfo()[i];
            ConsolePrintTable.Builder.RowDataBuilder rowDataBuilder = builder.getRowDataBuilder();
            rowDataBuilder.addData("ns", task.getTimeNanos());
            rowDataBuilder.addData("ms", task.getTimeMillis());
            rowDataBuilder.addData("s", ddf2.format(task.getTimeSeconds()));
            rowDataBuilder.addData("%", ddf1.format((double) task.getTimeNanos() / totalTimeNanos * 100));
            rowDataBuilder.addData("Task name", task.getTaskName());
            rowDataBuilder.addData("Task begin code", startLineInfos.get(i));
            rowDataBuilder.addData("Task end code", endLineInfos.get(i));
            builder.addRowData(rowDataBuilder.build());
        }
        return builder.build().prettyPrint();
    }

    private static String getCurrentCodeLineInfo(){
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        for (StackTraceElement stackTraceElement : stackTraceElements) {
            if (!stackTraceElement.getClassName().endsWith(StopWatchSingle.class.getName())) {
                return stackTraceElement.getClassName() + "#" + stackTraceElement.getMethodName() + "(" + stackTraceElement.getLineNumber() + ")";
            }
        }
        return "not find";
    }
}
