package io.github.simonalexs.tools;

import io.github.simonalexs.config.ToolConfig;
import io.github.simonalexs.enums.SAPropertyEnum;
import io.github.simonalexs.exceptions.MsgSendFailException;
import io.github.simonalexs.tools.senders.WxPusherUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class NotifyUtil {
    public static void notify(String summary, String content) throws MsgSendFailException {
        String notifyType = ToolConfig.getParam(SAPropertyEnum.CLOCK_NOTIFY_TYPES, String.class);
        if (StringUtils.isBlank(notifyType)) {
            NotifyUtil.notifyWechat(summary, content);
        } else {
            NotifyTypeEnum notifyTypeEnum = NotifyTypeEnum.parse(notifyType);
            notify(summary, content, notifyTypeEnum);
        }
    }

    public static void notify(String summary, String content, NotifyTypeEnum notifyType) throws MsgSendFailException {
        notify(summary, content, Collections.singleton(notifyType));
    }

    public static void notify(String summary, String content, Collection<NotifyTypeEnum> notifyTypes) throws MsgSendFailException {
        for (NotifyTypeEnum notifyType : notifyTypes) {
            switch (notifyType) {
                case WX_PUSHER:
                    NotifyUtil.notifyWechat(summary, content);
                    break;
                case EMAIL:
                    // TODO-normal：邮件提醒可以做。2024/02/29 11:01:17
                    break;
                case QQ:
                    // TODO-low：qq提醒不着急做 2024/02/29 11:01:22
                    break;
            }
        }
    }

    public static void notifyWechat(String summary, String content) throws MsgSendFailException {
        String appToken = ToolConfig.getParam(SAPropertyEnum.NOTIFY_WX_PUSHER_APP_TOKEN, String.class);
        String sendUIds = ToolConfig.getParam(SAPropertyEnum.NOTIFY_WX_PUSHER_SEND_UIDS, String.class);
        if (StringUtils.isBlank(appToken) || StringUtils.isBlank(sendUIds)) {
            WxPusherUtil.sendMd(summary, content);
        } else {
            WxPusherUtil.send(appToken, sendUIds, WxPusherUtil.WxPusherTypeEnum.MD, summary, content);
        }
    }

    public static void notifyWechat(String appToken, String sendUIdsStr, WxPusherUtil.WxPusherTypeEnum type,
                                    String summary, String content) throws MsgSendFailException {
        WxPusherUtil.send(appToken, sendUIdsStr, type, summary, content);
    }

    public enum NotifyTypeEnum {
        WX_PUSHER, EMAIL, QQ, SMS_TENCENT;

        private static final Map<String, NotifyTypeEnum> map = new LinkedHashMap<>();

        static {
            for (NotifyTypeEnum value : NotifyTypeEnum.values()) {
                String replacedName = value.name().replaceAll("_", "");
                map.put(value.name().toUpperCase(), value);
                map.put(replacedName.toUpperCase(), value);
            }
        }

        public static NotifyTypeEnum parse(String str) {
            if (!map.containsKey(str.toUpperCase())) {
                throw new RuntimeException("not support value [" + str + "].Supported notify enum value: "
                        + String.join(", ", map.keySet()));
            }
            return map.get(str.toUpperCase());
        }
    }
}
