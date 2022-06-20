package com.simonAlexs.tools.test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @ClassName: TestTemp
 * @Description: TODO-wcy
 * @Author: wcy
 * @Date: 2022/5/9 17:31
 * @Version: 1.0
 */
public class TestTemp {
    public static void main(String[] args) {
        List<String> list = new ArrayList<>();
        list.add("1");
        list.add("2");
        list.add("3");
        list.add("4");
        list.add("5");
        list.add("6");
        final List<String> collect = list.stream().flatMap(t -> Stream.of(t + "a", t + "b"))
                .collect(Collectors.toList());
        for (String s : collect) {
            System.out.println(s);
        }
    }
}
