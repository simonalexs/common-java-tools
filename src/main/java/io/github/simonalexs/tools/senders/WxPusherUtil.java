package io.github.simonalexs.tools.senders;

import com.zjiecode.wxpusher.client.WxPusher;
import com.zjiecode.wxpusher.client.bean.Message;
import com.zjiecode.wxpusher.client.bean.MessageResult;
import com.zjiecode.wxpusher.client.bean.Result;
import io.github.simonalexs.config.ToolConfig;
import io.github.simonalexs.enums.SAPropertyEnum;
import io.github.simonalexs.exceptions.MsgSendFailException;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class WxPusherUtil {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    private static final Map<Integer, String> LINE_SEPARATOR_MAP = new HashMap<Integer, String>(){{
        put(Message.CONTENT_TYPE_TEXT, "\n");
        put(Message.CONTENT_TYPE_HTML, "<br/>");
        put(Message.CONTENT_TYPE_MD, "<br/>");
    }};

    public static Result<List<MessageResult>> send(String summary, String content) throws MsgSendFailException {
        return send(WxPusherTypeEnum.TEXT, summary, content);
    }

    public static Result<List<MessageResult>> sendHtml(String summary, String content) throws MsgSendFailException {
        return send(WxPusherTypeEnum.HTML, summary, content);
    }

    public static Result<List<MessageResult>> sendMd(String summary, String content) throws MsgSendFailException {
        return send(WxPusherTypeEnum.MD, summary, content);
    }

    public static Result<List<MessageResult>> send(WxPusherTypeEnum type, String summary, String content) throws MsgSendFailException {
        Message message = new Message();
        message.setAppToken(ToolConfig.getParamAndCheck(SAPropertyEnum.WX_PUSHER_APP_TOKEN, String.class));
        message.setContentType(type.getValue());

        SAPropertyEnum uidKey = SAPropertyEnum.WX_PUSHER_SEND_UIDS;
        String uIdsStr = ToolConfig.getParamAndCheck(uidKey, String.class);
        Set<String> uIds = ToolConfig.getUIdSetByStr(uIdsStr);
        if (uIds.isEmpty()) {
            throw new RuntimeException(ToolConfig.getParamErrorInfo(uidKey));
        }
        message.setUids(uIds);
        message.setSummary(summary);
        message.setContent(content);
        return send(message);
    }

    public static Result<List<MessageResult>> send(String appToken, String sendUIdsStr, WxPusherTypeEnum type,
                                                   String summary, String content) throws MsgSendFailException {
        Set<String> uIds = ToolConfig.getUIdSetByStr(sendUIdsStr);
        return send(appToken, uIds, type, summary, content);
    }

    public static Result<List<MessageResult>> send(String appToken, Set<String> sendUIds, WxPusherTypeEnum type,
                                                   String summary, String content) throws MsgSendFailException {
        Message message = new Message();
        message.setAppToken(appToken);
        message.setContentType(type.getValue());
        message.setUids(sendUIds);
        message.setSummary(summary);
        message.setContent(content);
        return send(message);
    }

    public static Result<List<MessageResult>> send(Message message) throws MsgSendFailException {
        // TODO-high：校验必填项是否为空。2024/02/29 10:45:52
        if (message.getContentType() == Message.CONTENT_TYPE_MD
                && StringUtils.isNotEmpty(message.getContent())) {
            // 解决发送到微信上时，英文的双引号显示成中文引号的问题
            String newContent = message.getContent().replaceAll("\"", "`\"`");
            message.setContent(newContent);
        }
        // 添加时间后缀
        String nowTime = LocalDateTime.now().format(DATE_TIME_FORMATTER);
        String lineSeparator = LINE_SEPARATOR_MAP.get(message.getContentType());
        message.setContent(message.getContent() + lineSeparator + lineSeparator + nowTime);
        Result<List<MessageResult>> result = WxPusher.send(message);
        if (!result.isSuccess()) {
            throw new MsgSendFailException(result.getMsg());
        }
        return result;
    }

    public enum WxPusherTypeEnum {
        TEXT(Message.CONTENT_TYPE_TEXT),
        HTML(Message.CONTENT_TYPE_HTML),
        MD(Message.CONTENT_TYPE_MD);

        final int value;
        WxPusherTypeEnum(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
}
