package io.github.simonalexs.enums;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public enum NotifyTypeEnum {
    WX_PUSHER, EMAIL, QQ;

    private static final Map<String, NotifyTypeEnum> map = new LinkedHashMap<>();

    static {
        for (NotifyTypeEnum value : NotifyTypeEnum.values()) {
            String replacedName = value.name().replaceAll("_", "");
            map.put(value.name().toUpperCase(), value);
            map.put(replacedName.toUpperCase(), value);
        }
    }

    public static NotifyTypeEnum parse(String str) {
        if (!map.containsKey(str.toUpperCase())) {
            throw new RuntimeException("not support value [" + str + "].Supported notify enum value: "
                    + String.join(", ", map.keySet()));
        }
        return map.get(str.toUpperCase());
    }
}
