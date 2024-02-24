package com.simonalexs.tools.test.algorithm;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.simonalexs.tools.algorithm.LeetCodeQuestions;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Random;

/**
 * @ClassName: OtherAlgorithm
 * @Description: TODO-wcy
 * @Author: wcy
 * @Date: 2022/6/20 09:48
 * @Version: 1.0
 */
public class LeetCodeQuestionsTest {
    public static Thread thread;
    public static void main(String[] args) throws InterruptedException, NoSuchFieldException, IllegalAccessException {
//        test1723();

        String a = "a2";
        final A a1 = new A();
        System.out.println(a1.getA2());
        final Field field = a1.getClass().getDeclaredField(a);
        field.setAccessible(true);
        field.set(a1, 9);
        System.out.println(a1.getA2());
        a1.getClass().getDeclaredField(a).set(a1, 12);
        System.out.println(a1.getA2());
    }

    public static String removeStrsFromSeparatedString(String separatedString, List<String> strsToRemove, String separator) {
        String resultStrWithSeparator = separatedString + separator;
        for (String strToRemove : strsToRemove) {
            resultStrWithSeparator = resultStrWithSeparator.replace(strToRemove + separator, "");
        }
        // 去除末尾的分隔符
        return resultStrWithSeparator.substring(0, resultStrWithSeparator.length() - separator.length());
    }

    public static void test1723() {
        int[] jobs = new int[5];
        final Random random = new Random();
        for (int i = 0; i < jobs.length; i++) {
            jobs[i] = random.nextInt(10) + 1;
        }
        int k = 3;
        print(jobs);
        final long l = LeetCodeQuestions.q1723(jobs, k);
        print(l);
    }

    public static void print(Object o) {
        System.out.println(JSON.toJSONString(o, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue));
    }
}
