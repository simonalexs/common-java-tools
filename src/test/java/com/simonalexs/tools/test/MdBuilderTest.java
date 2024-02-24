package com.simonalexs.tools.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.simonalexs.tools.base.baseStruct.ConsolePrintCellConfig;
import com.simonalexs.tools.other.MdBuilder;

/**
 * @ClassName: MdBuilderDemo
 * @Description: TODO-wcy
 * @Author: wcy
 * @Date: 2022/5/10 11:17
 * @Version: 1.0
 */
public class MdBuilderTest {
    public static void main(String[] args) {
        MdBuilder contentBuilder = MdBuilder.getBuilder();
        contentBuilder.text("测试").lineBreak()
                .prepareForListWithUnordered()
                .listWithUnordered().blod("标题：").text("roomInfoBySpecialApi.getRoom_name()").lineBreak()
                .listWithUnordered().blod("本次直播时长：").text("liveTimeStr").lineBreak();
        String build = contentBuilder.build();
        System.out.println(build);

        ConsolePrintCellConfig consolePrintCellConfig = new ConsolePrintCellConfig();
        String s = JSON.toJSONString(consolePrintCellConfig, SerializerFeature.WriteMapNullValue, SerializerFeature.QuoteFieldNames, SerializerFeature.PrettyFormat);

        MdBuilder contentBuilder2 = MdBuilder.getBuilder();
        contentBuilder.text(s).lineBreak()
                .text("\"\", `\"``\"`")
                .text(s.replaceAll("\"", "`\"`"));
        String build2 = contentBuilder2.build();
        System.out.println(build2);
    }
}
