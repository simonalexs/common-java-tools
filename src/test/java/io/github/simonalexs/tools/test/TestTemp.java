package io.github.simonalexs.tools.test;

import com.alibaba.fastjson.JSON;

import java.math.BigDecimal;
import java.util.Arrays;
import net.sf.json.JSONObject;

/**
 * @ClassName: TestTemp
 * @Description: TODO-wcy
 * @Author: wcy
 * @Date: 2022/5/9 17:31
 * @Version: 1.0
 */
public class TestTemp {
    public static void main(String[] args) {
        System.out.println(JSON.toJSONString(Arrays.asList("1", "2")));


        // 创建一个JSONObject实例
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("key1", "value1");
        jsonObject.put("key2", "value2");

        // 将JSONObject转换为String
        String jsonString = jsonObject.toString();

        // 打印转换后的String
        System.out.println(jsonString);
    }
}
