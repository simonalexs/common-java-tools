package io.github.simonalexs.base.baseStruct;


import io.github.simonalexs.tools.debug.StopWatch;

/**
 * StopWatchInfo
 */
public class StopWatchInfo{
    private StopWatch stopWatch = new StopWatch();
    private String taskName = "";
    private String startLineInfo = "";
    private String endLineInfo = "";

    public StopWatch getStopWatch() {
        return stopWatch;
    }

    public void setStopWatch(StopWatch stopWatch) {
        this.stopWatch = stopWatch;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getStartLineInfo() {
        return startLineInfo;
    }

    public void setStartLineInfo(String startLineInfo) {
        this.startLineInfo = startLineInfo;
    }

    public String getEndLineInfo() {
        return endLineInfo;
    }

    public void setEndLineInfo(String endLineInfo) {
        this.endLineInfo = endLineInfo;
    }
}
