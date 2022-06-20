package com.simonAlexs.tools.other;

import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

/**
 * @ClassName: ConnectStrsUtil
 * @Description: TODO-wcy
 * @Author: wcy
 * @Date: 2022/5/9 17:32
 * @Version: 1.0
 */
public class ConnectStrsUtil {
    /**
     * 从第一个非0的元素开始拼接，直到最后。
     * 例如：传入 "0 天 5 小时 0 分钟 17秒"，输出 "5 小时 0 分钟 17秒"
     * @return
     */
    public static String connectStrFromNoneZero(List<Pair<Integer, String>> keyAndConnectStrPairList) {
        boolean isFindNoneZero = false;
        StringBuilder result = new StringBuilder();
        for (Pair<Integer, String> keyAndConnectStrPair : keyAndConnectStrPairList) {
            if (!isFindNoneZero) {
                if (keyAndConnectStrPair.getKey().intValue() != 0) {
                    isFindNoneZero = true;
                } else {
                    continue;
                }
            }
            result.append(keyAndConnectStrPair.getValue());
        }
        return result.toString();
    }
}
