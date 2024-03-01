package io.github.simonalexs.tools;

import io.github.simonalexs.config.ToolConfig;
import io.github.simonalexs.enums.NotifyTypeEnum;
import io.github.simonalexs.enums.WxPusherTypeEnum;
import io.github.simonalexs.exceptions.MsgSendFailException;
import org.apache.commons.lang3.StringUtils;

public class ClockUtil {
    // TODO-high：添加定时提醒功能。2024/02/29 09:49:28
    // TODO-high：要有一个传入function的方法，预设条件满足时再触发提醒。2024/02/29 09:49:44
    // TODO-high：目标时间频率（每天几点、每月几点、每年的某一天几点）。2024/02/29 09:50:44
    // TODO-high：目标时间之前的提醒间隔（提前多少分钟每几分钟提醒一次，提前多少小时每多长时间提醒一次，提前多少天每天提醒一次）。2024/02/29 09:50:44
    // TODO-high：提醒方式（微信提醒wxpusher、邮件提醒、qq提醒）。2024/02/29 09:50:44

    // 提醒触发条件：到达指定时间；
    //            出现指定状态；
    // 提醒方式：
    // 提醒的开始时间、提醒间隔、提醒的截止时间、如何远程停止本提醒

}
