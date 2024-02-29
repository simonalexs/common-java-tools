package io.github.simonalexs.enums;

import com.zjiecode.wxpusher.client.bean.Message;

import javax.swing.text.html.HTML;

public enum WxPusherTypeEnum {
    TEXT(Message.CONTENT_TYPE_TEXT),
    HTML(Message.CONTENT_TYPE_HTML),
    MD(Message.CONTENT_TYPE_MD);

    final int value;
    WxPusherTypeEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
