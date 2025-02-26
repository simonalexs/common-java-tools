package io.github.simonalexs.tools.test.senders;

import com.github.qcloudsms.SmsSingleSenderResult;
import io.github.simonalexs.tools.other.PrintUtil;
import io.github.simonalexs.tools.senders.SmsUtil;

import java.util.Collections;

public class SmsUtilTest {
    public static void main(String[] args) {
        SmsSingleSenderResult result = SmsUtil.sendByTencent2("13612341234", 384987, Collections.singletonList("测试短信3"));
        PrintUtil.printlnPretty(result);
    }
}
