package io.github.simonalexs.tools;

import com.zjiecode.wxpusher.client.WxPusher;
import com.zjiecode.wxpusher.client.bean.Message;
import com.zjiecode.wxpusher.client.bean.MessageResult;
import com.zjiecode.wxpusher.client.bean.Result;
import io.github.simonalexs.config.ToolConfig;
import io.github.simonalexs.enums.WxPusherTypeEnum;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class WxPusherUtil {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    private static final Map<Integer, String> LINE_SEPARATOR_MAP = new HashMap<Integer, String>(){{
        put(Message.CONTENT_TYPE_TEXT, "\n");
        put(Message.CONTENT_TYPE_HTML, "<br/>");
        put(Message.CONTENT_TYPE_MD, "<br/>");
    }};

    public static Result<List<MessageResult>> send(Message message) {
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
        return WxPusher.send(message);
    }

    public static Result<List<MessageResult>> send(String summary, String content) {
        return doSend(WxPusherTypeEnum.TEXT, summary, content);
    }

    public static Result<List<MessageResult>> sendHtml(String summary, String content) {
        return doSend(WxPusherTypeEnum.HTML, summary, content);
    }

    public static Result<List<MessageResult>> sendMd(String summary, String content) {
        return doSend(WxPusherTypeEnum.MD, summary, content);
    }

    private static Result<List<MessageResult>> doSend(WxPusherTypeEnum type, String summary, String content) {
        Message message = new Message();
        message.setAppToken(ToolConfig.getParamAndCheck("wxpusher.appToken", String.class));
        message.setContentType(type.getValue());
        message.setUids(getSendUids());
        message.setSummary(summary);
        message.setContent(content);
        return send(message);
    }

    private static Set<String> getSendUids() {
        String uidKey = "wxpusher.sendUIds";
        String uidsStr = ToolConfig.getParamAndCheck(uidKey, String.class);
        List<?> uidsList = Arrays.asList(StringUtils.split(uidsStr, ","));
        Set<String> uids = uidsList.stream()
                .map(t -> t == null ? null : t.toString().trim())
                .filter(StringUtils::isNotEmpty)
                .collect(Collectors.toSet());
        if (uids.isEmpty()) {
            throw new RuntimeException(ToolConfig.getParamErrorInfo(uidKey));
        }
        return uids;
    }
}
