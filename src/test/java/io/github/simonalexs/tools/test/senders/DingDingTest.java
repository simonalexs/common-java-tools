package io.github.simonalexs.tools.test.senders;

import com.zjiecode.wxpusher.client.bean.MessageResult;
import com.zjiecode.wxpusher.client.bean.Result;
import io.github.simonalexs.config.ToolConfig;
import io.github.simonalexs.exceptions.MsgSendFailException;
import io.github.simonalexs.tools.other.PrintUtil;
import io.github.simonalexs.tools.senders.DingDing;
import io.github.simonalexs.tools.senders.WxPusherUtil;
import org.junit.jupiter.api.Test;

import java.util.List;

public class DingDingTest {

    @Test
    public void test() throws ClassNotFoundException, MsgSendFailException {
        Class.forName(ToolConfig.class.getName());

        DingDing.sendToRobot("你好");
    }
}
