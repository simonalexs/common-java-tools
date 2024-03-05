package io.github.simonalexs.tools.test;

import com.alibaba.fastjson.JSON;

import java.math.BigDecimal;

/**
 * @ClassName: TestTemp
 * @Description: TODO-wcy
 * @Author: wcy
 * @Date: 2022/5/9 17:31
 * @Version: 1.0
 */
public class TestTemp {
    public static void main(String[] args) {
        System.out.println(JSON.toJSONString(new BigDecimal("33.78")));
    }
}
