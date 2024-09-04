package io.github.simonalexs.tools.test;

import io.github.simonalexs.enums.SAPropertyEnum;
import io.github.simonalexs.tools.other.PrintUtil;
import io.github.simonalexs.tools.test.algorithm.A;
import org.junit.jupiter.api.Test;

public class PrintUtilTest {
    @Test
    public void test1() {
//        PrintUtil.println(null);
//        PrintUtil.println("st\"r", "s1", "s2", "s3");
//        PrintUtil.println(1);
//        PrintUtil.println(1.3);
        A a = new A();
        a.setA3(4);
        PrintUtil.printlnPretty(a);
//        PrintUtil.println(Arrays.asList(1, 3, 5, 8));
//
//        PrintUtil.printList(Arrays.asList(1, 3, 5, 8), Arrays.asList(1, 3, 5, 8));
//        PrintUtil.printList(Arrays.asList(1, 3, 5, 8));
//        PrintUtil.printList("-", Arrays.asList(1, 3, 5, 8), Arrays.asList(1, 3, 5, 8));
//        PrintUtil.printList("=", "-", "=",  Arrays.asList(1, 3, 5, 8), Arrays.asList(1, 3, 5, 8));

        PrintUtil.println(SAPropertyEnum.CLOCK_NOTIFY_TYPES);
    }
}
