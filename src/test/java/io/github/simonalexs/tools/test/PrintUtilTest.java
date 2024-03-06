package io.github.simonalexs.tools.test;

import com.alibaba.fastjson.JSON;
import io.github.simonalexs.tools.other.PrintUtil;
import io.github.simonalexs.tools.test.algorithm.A;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

public class PrintUtilTest {
    @Test
    public void test1() {
        PrintUtil.println(null);
        PrintUtil.println("st\"r", "s1", "s2", "s3");
        PrintUtil.println(1);
        PrintUtil.println(1.3);
        PrintUtil.println(new A());
        PrintUtil.println(Arrays.asList(1, 3, 5, 8));

        PrintUtil.printList(Arrays.asList(1, 3, 5, 8), Arrays.asList(1, 3, 5, 8));
        PrintUtil.printList(Arrays.asList(1, 3, 5, 8));
        PrintUtil.printList("-", Arrays.asList(1, 3, 5, 8), Arrays.asList(1, 3, 5, 8));
        PrintUtil.printList("=", "-", "=",  Arrays.asList(1, 3, 5, 8), Arrays.asList(1, 3, 5, 8));
    }
}
