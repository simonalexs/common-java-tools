package io.github.simonalexs.tools.senders;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.dingtalk.api.response.OapiRobotSendResponse;
import io.github.simonalexs.base.SResult;
import io.github.simonalexs.config.ToolConfig;
import io.github.simonalexs.enums.SAPropertyEnum;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

public class DingDing {

    /**
     * 通过钉钉机器人发送消息到钉钉群（需要群里添加 自定义机器人）
     */
    public static SResult sendToRobot(String content) {
        return sendToRobot(content, "");
    }

    /**
     * 通过钉钉机器人发送消息到钉钉群（需要群里添加 自定义机器人）
     * @param atUserIds 内部群才可以使用 @人 的功能
     * @return errcode 0代表成功
     */
    public static SResult sendToRobot(String content, String atUserIds) {
        // <your custom robot token>
        String token = ToolConfig.getParamAndCheck(SAPropertyEnum.DING_DING_ROBOT_WEBHOOK_ACCESS_TOKEN, String.class);
        String secret = ToolConfig.getParamAndCheck(SAPropertyEnum.DING_DING_ROBOT_WEBHOOK_SECRET, String.class);
        try {
            Long timestamp = System.currentTimeMillis();
            String stringToSign = timestamp + "\n" + secret;
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] signData = mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));
            String sign = URLEncoder.encode(new String(Base64.encodeBase64(signData)),"UTF-8");

            //sign字段和timestamp字段必须拼接到请求URL上，否则会出现 310000 的错误信息
            DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/robot/send?sign="+sign+"&timestamp="+timestamp);
            OapiRobotSendRequest req = new OapiRobotSendRequest();
            //定义文本内容
            OapiRobotSendRequest.Text text = new OapiRobotSendRequest.Text();
            text.setContent(content);
            //定义 @ 对象
            OapiRobotSendRequest.At at = new OapiRobotSendRequest.At();
            if (StringUtils.isNotBlank(atUserIds)) {
                at.setAtUserIds(Collections.singletonList(atUserIds));
            }
            //设置消息类型
            req.setMsgtype("text");
            req.setText(text);
            req.setAt(at);
            OapiRobotSendResponse rsp = client.execute(req, token);
            JSONObject jsonObject = JSON.parseObject(rsp.getBody());
            Integer errcode = jsonObject.getInteger("errcode");
            if (Integer.valueOf(0).equals(errcode)) {
                return SResult.success();
            } else {
                return SResult.error(errcode == null ? -1 : errcode, jsonObject.getString("errmsg"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return SResult.error(e.getMessage());
        }
    }
}
