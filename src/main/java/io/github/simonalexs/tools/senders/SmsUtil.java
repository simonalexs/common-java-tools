package io.github.simonalexs.tools.senders;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import com.github.qcloudsms.SmsMultiSender;
import com.github.qcloudsms.SmsMultiSenderResult;
import com.github.qcloudsms.SmsSingleSender;
import com.github.qcloudsms.SmsSingleSenderResult;
import com.github.qcloudsms.httpclient.HTTPException;
import com.tencentcloudapi.common.CommonClient;
import com.tencentcloudapi.common.CommonRequest;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.Region;
import com.tencentcloudapi.common.provider.EnvironmentVariableCredentialsProvider;
import io.github.simonalexs.tools.other.PrintUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * 发送短信的工具类
 */
public class SmsUtil {
    //region tencent 2.0
    /**
     * 环境变量配置：TENCENTCLOUD_SECRET_ID，TENCENTCLOUD_SECRET_KEY
     * 依赖腾讯 2.0 sdk
     * 参考：<a href="https://cloud.tencent.com/document/product/382/13613#mbdf">2.0 sdk文档</a>
     * @param phoneNumber 要发送到的目标手机号
     * @param templateId 短信模版id，是在腾讯云上创建的模版
     * @param paramsForTemplate 实际要发送的参数，对应于该短信模板中的参数数量
     * @return 成功返回空字符串，否则返回错误提示
     */
    public static SmsSingleSenderResult sendByTencent2(String phoneNumber, int templateId, Collection<String> paramsForTemplate) {
        try {
            Credential credential = new EnvironmentVariableCredentialsProvider().getCredentials();
            return sendByTencent2(credential, phoneNumber, templateId, paramsForTemplate);
        } catch (TencentCloudSDKException e) {
            return ResultProducer.buildSmsSingleSenderResult(e);
        }
    }

    /**
     * 依赖腾讯 2.0 sdk
     * 参考：<a href="https://cloud.tencent.com/document/product/382/13613#mbdf">2.0 sdk文档</a>
     * @param credential 存有 secretId, secretKey的实体
     * @param phoneNumber 要发送到的目标手机号
     * @param templateId 短信模版id，是在腾讯云上创建的模版
     * @param paramsForTemplate 实际要发送的参数，对应于该短信模板中的参数数量
     * @return 成功返回空字符串，否则返回错误提示
     */
    public static SmsSingleSenderResult sendByTencent2(Credential credential, String phoneNumber, int templateId, Collection<String> paramsForTemplate) {
        try {
            SmsSingleSender sender = new SmsSingleSender(Integer.parseInt(credential.getSecretId()), credential.getSecretKey());
            // nationCode: 86 代表china
            return sender.sendWithParam("86", phoneNumber,
                    templateId, new ArrayList<>(paramsForTemplate), "", "", "");
        } catch (HTTPException | IOException e) {
            return ResultProducer.buildSmsSingleSenderResult(e);
        }
    }

    /**
     * 环境变量配置：TENCENTCLOUD_SECRET_ID，TENCENTCLOUD_SECRET_KEY
     * 依赖腾讯 2.0 sdk
     * 参考：<a href="https://cloud.tencent.com/document/product/382/13613#mbdf">2.0 sdk文档</a>
     * @param phoneNumbers 要发送到的目标手机号列表
     * @param templateId 短信模版id，是在腾讯云上创建的模版
     * @param paramsForTemplate 实际要发送的参数，对应于该短信模板中的参数数量
     * @return 成功返回空字符串，否则返回错误提示
     */
    public static SmsMultiSenderResult sendByTencent2(Collection<String> phoneNumbers, int templateId, Collection<String> paramsForTemplate) {
        try {
            Credential credential = new EnvironmentVariableCredentialsProvider().getCredentials();
            return sendByTencent2(credential, phoneNumbers, templateId, paramsForTemplate);
        } catch (TencentCloudSDKException e) {
            return ResultProducer.buildSmsMultiSenderResult(e);
        }
    }

    /**
     * 依赖腾讯 2.0 sdk
     * 参考：<a href="https://cloud.tencent.com/document/product/382/13613#mbdf">2.0 sdk文档</a>
     * @param credential 存有 secretId, secretKey的实体
     * @param phoneNumbers 要发送到的目标手机号列表
     * @param templateId 短信模版id，是在腾讯云上创建的模版
     * @param paramsForTemplate 实际要发送的参数，对应于该短信模板中的参数数量
     * @return 成功返回空字符串，否则返回错误提示
     */
    public static SmsMultiSenderResult sendByTencent2(Credential credential, Collection<String> phoneNumbers, int templateId, Collection<String> paramsForTemplate) {
        try {
            SmsMultiSender sender = new SmsMultiSender(Integer.parseInt(credential.getSecretId()), credential.getSecretKey());
            // nationCode: 86 代表china
            return sender.sendWithParam("86", new ArrayList<>(phoneNumbers),
                    templateId, new ArrayList<>(paramsForTemplate), "", "", "");
        } catch (HTTPException | IOException e) {
            return ResultProducer.buildSmsMultiSenderResult(e);
        }
    }
    //endregion

    //region tencent 3.0 -- 未测试
    /**
     * 依赖腾讯 3.0 sdk
     * <a href="https://github.com/TencentCloud/tencentcloud-sdk-java/blob/master/examples/common/commonclient">代码参考</a>
     * <a href="https://cloud.tencent.com/document/product/382/43194">3.0 sdk文档</a>
     * @param productName 要调用的腾讯云短信产品名（例如 cvm）
     * @param version 要调用的腾讯云短信服务器版本（例如2021-01-11，2017-03-12）
     * @param region 地区
     */
    public static String sendByTencent3(String productName, String version, Region region, Map<String, Object> jsonPayload) {
        try {
            Credential credential = new EnvironmentVariableCredentialsProvider().getCredentials();
            return sendByTencent3(productName, version, credential, region, jsonPayload);
        } catch (TencentCloudSDKException e) {
            return e.getMessage();
        }
    }
    /**
     * 依赖腾讯 3.0 sdk
     * <a href="https://github.com/TencentCloud/tencentcloud-sdk-java/blob/master/examples/common/commonclient">代码参考</a>
     * <a href="https://cloud.tencent.com/document/product/382/43194">3.0 sdk文档</a>
     * @param productName 要调用的腾讯云短信产品名（例如 cvm）
     * @param version 要调用的腾讯云短信服务器版本（例如2021-01-11，2017-03-12）
     * @param credential 存有 secretId, secretKey的实体
     * @param region 地区
     */
    public static String sendByTencent3(String productName, String version, Credential credential, Region region,
                                      Map<String, Object> jsonPayload) {
        try {
            CommonClient client = new CommonClient(productName, version, credential, region.getValue());
            return client.call("SendSms", JSON.toJSONString(jsonPayload, JSONWriter.Feature.WriteMapNullValue));
        } catch (TencentCloudSDKException e) {
            return e.getMessage();
        }
    }
    //endregion

    private static class ResultProducer {
        public static SmsMultiSenderResult buildSmsMultiSenderResult(Exception e) {
            SmsMultiSenderResult result = new SmsMultiSenderResult();
            result.result = -1;
            result.errMsg = e.getMessage();
            return result;
        }
        public static SmsSingleSenderResult buildSmsSingleSenderResult(Exception e) {
            SmsSingleSenderResult result = new SmsSingleSenderResult();
            result.result = -1;
            result.errMsg = e.getMessage();
            return result;
        }
    }
}
