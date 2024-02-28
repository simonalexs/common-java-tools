package io.github.simonalexs.tools.test;

import io.github.simonalexs.tools.other.PrintUtil;
import io.github.simonalexs.tools.test.algorithm.A;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

public class PrintUtilTest {
    @Test
    public void test1() {
        PrintUtil.println("str");
        PrintUtil.println(1);
        PrintUtil.println(1.3);
        PrintUtil.println(new A());
        PrintUtil.println(Arrays.asList(1, 3, 5, 8));
    }
}
