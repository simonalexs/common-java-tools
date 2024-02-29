package io.github.simonalexs.tools.test;

import com.zjiecode.wxpusher.client.bean.Message;
import com.zjiecode.wxpusher.client.bean.MessageResult;
import com.zjiecode.wxpusher.client.bean.Result;
import io.github.simonalexs.config.ToolConfig;
import io.github.simonalexs.enums.WxPusherTypeEnum;
import io.github.simonalexs.tools.WxPusherUtil;
import io.github.simonalexs.tools.other.PrintUtil;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class WxPusherUtilTest {

    @Test
    public void test() throws ClassNotFoundException {
        Class.forName(ToolConfig.class.getName());

        Result<List<MessageResult>> result = WxPusherUtil.sendMd("1", "2");
        PrintUtil.println(result);
    }
}
