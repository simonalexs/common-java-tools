package io.github.simonalexs.tools.test;

import com.zjiecode.wxpusher.client.bean.MessageResult;
import com.zjiecode.wxpusher.client.bean.Result;
import io.github.simonalexs.config.ToolConfig;
import io.github.simonalexs.exceptions.MsgSendFailException;
import io.github.simonalexs.tools.senders.WxPusherUtil;
import io.github.simonalexs.tools.other.PrintUtil;
import org.junit.jupiter.api.Test;

import java.util.List;

public class WxPusherUtilTest {

    @Test
    public void test() throws ClassNotFoundException, MsgSendFailException {
        Class.forName(ToolConfig.class.getName());

        Result<List<MessageResult>> result = WxPusherUtil.sendMd("1", "2");
        PrintUtil.println(result);
    }
}
