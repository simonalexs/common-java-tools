package com.simonAlexs.tools.base.baseStruct;

import lombok.Data;
import org.springframework.util.StopWatch;

/**
 * @ClassName: StopWatchInfo
 * @Description: TODO-wcy
 * @Author: wcy
 * @Date: 2022/4/19 08:15
 * @Version: 1.0
 */
@Data
public class StopWatchInfo{
    private StopWatch stopWatch = new StopWatch();
    private String taskName = "";
    private String startLineInfo = "";
    private String endLineInfo = "";
}
