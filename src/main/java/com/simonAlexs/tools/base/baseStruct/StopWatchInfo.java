package com.simonAlexs.tools.base.baseStruct;


import com.simonAlexs.tools.debugUtil.StopWatch;

/**
 * @ClassName: StopWatchInfo
 * @Description: TODO-wcy
 * @Author: wcy
 * @Date: 2022/4/19 08:15
 * @Version: 1.0
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
