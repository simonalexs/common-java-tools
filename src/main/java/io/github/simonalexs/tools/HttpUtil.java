package io.github.simonalexs.tools;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.core5.http.HttpStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpUtil {
//    public static String post(String url, Map<String, Object> param) {
//        return post(url, JSON.toJSONString(param, JSONWriter.Feature.WriteMapNullValue));
//    }
//    public static String post(String url, String jsonStr) {
//        String contentType = "application/json";
//        try {
//            HttpClient client = new HttpClient();
//            // 连接超时
//            client.getHttpConnectionManager().getParams().setConnectionTimeout(3 * 1000);
//            // 读取数据超时
//            client.getHttpConnectionManager().getParams().setSoTimeout(3 * 60 * 1000);
//            client.getParams().setContentCharset("UTF-8");
//            PostMethod postMethod = new PostMethod(url);
//
//            postMethod.setRequestHeader("Content-Type", contentType);
//            // 非空
//            if (null != jsonStr && !jsonStr.isEmpty()) {
//                StringRequestEntity requestEntity = new StringRequestEntity(jsonStr, contentType, "UTF-8");
//                postMethod.setRequestEntity(requestEntity);
//            }
//            int status = client.executeMethod(postMethod);
//            if (status == HttpStatus.SC_OK) {
//                return postMethod.getResponseBodyAsString();
//            } else {
//                throw new RuntimeException("接口连接失败！");
//            }
//        } catch (Exception e) {
//            throw new RuntimeException("接口连接失败！");
//        }
//    }
}
