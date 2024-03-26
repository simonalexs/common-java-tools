package io.github.simonalexs.enums;

import io.github.simonalexs.tools.NotifyUtil;

public enum SAPropertyEnum {
    WX_PUSHER_APP_TOKEN("wxpusher.appToken"),
    WX_PUSHER_SEND_UIDS("wxpusher.sendUIds"),

    NOTIFY_WX_PUSHER_APP_TOKEN("notify.wxpusher.appToken"),
    NOTIFY_WX_PUSHER_SEND_UIDS("notify.wxpusher.sendUIds"),

    CLOCK_NOTIFY_TYPES("clock.notify.types", NotifyUtil.NotifyTypeEnum.WX_PUSHER.name()),

    ;
    // TODO-high：将配置文件中的配置项名，设置到枚举中。2024/02/29 14:04:27


    private final String name;
    private final String defaultValue;

    SAPropertyEnum(String name, String defaultValue) {
        this.name = name;
        this.defaultValue = defaultValue;
    }

    SAPropertyEnum(String name) {
        this.name = name;
        this.defaultValue = "";
    }

    public String getName() {
        return name;
    }
}
