package io.github.simonalexs.tools.algorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;

/**
 * TreeUtil
 */
public class TreeUtil {
    /**
     * {@link TreeUtil#filterSubNodeForLongIds(java.util.Collection, java.lang.String)}方法中需使用的自定义分隔符，保证排序后为严格的树结构（"ascll = 0"的ascll码值最小，可以基本保证排序时为树结构）<br/>
     * 提示：若需debug调试算法，可将此分隔符替换为@、!等可见字符，便于调试观测
     */
    private static final String SEPARATOR_IN_FILTER_SUBNODE_FOR_LONG_IDS = String.valueOf((char) 0);
    /**
     * {@link TreeUtil#filterSubNodeForLongIds(java.util.Collection, java.lang.String)}方法中需使用的临界值，依据此值决定使用哪种算法。
     */
    private static final int THRESHOLD_LENGTH_IN_FILTER_SUBNODE_FOR_LONG_IDS = 200;

    /**
     * 筛选树节点长名列表中的“最子节点”
     * @param collection : 树节点长名（全路径名）的集合
     * @param separator : 树节点长名（全路径名）所使用的分隔符
     * @return 筛选后的“最子节点”列表
     */
    public static ArrayList<String> filterSubNodeForLongIds(Collection<String> collection, String separator) {
        return filterSubNodeForLongIds(collection.toArray(new String[0]), separator);
    }

    /**
     * 筛选树节点长名列表中的“最子节点”
     * @param inputArray : 树节点长名（全路径名）的数组
     * @param separator : 树节点长名（全路径名）所使用的分隔符
     * @return 筛选后的“最子节点”列表
     */
    public static ArrayList<String> filterSubNodeForLongIds(String[] inputArray, String separator) {
        ArrayList<String> resultList = new ArrayList<>();
        if (inputArray == null || inputArray.length == 0) {
            return resultList;
        }

        // 依据元素数量，判断使用哪种方法，以追求效率
        if (inputArray.length <= THRESHOLD_LENGTH_IN_FILTER_SUBNODE_FOR_LONG_IDS) {
            resultList = doFilterSubNodeByCommonLoop(inputArray, separator);
        } else {
            resultList = doFilterSubNodeBySortAndTreeLevel(inputArray, separator);
        }

        return resultList;
    }

    /**
     * 数据量小时可以使用此方法，因为{@link TreeUtil#doFilterSubNodeBySortAndTreeLevel(java.lang.String[], java.lang.String)}方法中有前置及后置处理操作，会消耗一定时间
     */
    private static ArrayList<String> doFilterSubNodeByCommonLoop(String[] inputArray, String separator) {
        ArrayList<String> resultList = new ArrayList<>();
        Arrays.stream(inputArray).forEach(t -> {
            if (Arrays.stream(inputArray).noneMatch(s -> s.startsWith(t + separator))) {
                resultList.add(t);
            }
        });
        return resultList;
    }

    /**
     * 数据量大时效率较高，经测试，10万数据量耗时900ms左右，100万数据量耗时10s左右<br/>
     * 后续优化思路：若还需提升性能，可以考虑将此算法内涉及的三项操作（除Arrays.sort方法外：1.）
     */
    private static ArrayList<String> doFilterSubNodeBySortAndTreeLevel(String[] inputArray, String separator) {
        ArrayList<String> resultList = new ArrayList<>();
        // 将分隔符替换为自定义分隔符，保证排序后为严格的树结构（"ascll = 0"的ascll码值最小，可以基本保证排序时为树结构）
        String[] customArray = new String[inputArray.length];
        boolean isSeparatorSame = SEPARATOR_IN_FILTER_SUBNODE_FOR_LONG_IDS.equals(separator);
        if (isSeparatorSame) {
            customArray = Arrays.copyOf(inputArray, inputArray.length);
        } else {
            for (int i = 0; i < inputArray.length; i++) {
                customArray[i] = inputArray[i].replaceAll(separator, SEPARATOR_IN_FILTER_SUBNODE_FOR_LONG_IDS);
            }
        }

        // 排序数组（排列后的样子是严格的树结构），为后续判断做准备
        Arrays.sort(customArray, Comparator.naturalOrder());
        int thisNodeLevel = (customArray[0].length() - customArray[0].replaceAll(SEPARATOR_IN_FILTER_SUBNODE_FOR_LONG_IDS, "").length()) / SEPARATOR_IN_FILTER_SUBNODE_FOR_LONG_IDS.length();
        // 从第一个节点遍历至倒数第二个节点，判断其是否为最子节点
        for (int i = 0; i <= customArray.length - 2; i++) {
            int postNodeLevel = (customArray[i + 1].length() - customArray[i + 1].replaceAll(SEPARATOR_IN_FILTER_SUBNODE_FOR_LONG_IDS, "").length()) / SEPARATOR_IN_FILTER_SUBNODE_FOR_LONG_IDS.length();
            if (postNodeLevel <= thisNodeLevel) {
                // 下一个节点的层级小于或等于当前节点层级，则当前节点一定是最子节点
                resultList.add(customArray[i]);
            } else {
                // 下一个节点的层级大于当前节点层级，有两种情况：下一个节点是当前节点的子节点；下一个节点不是当前节点的子节点
                if (!customArray[i + 1].startsWith(customArray[i] + SEPARATOR_IN_FILTER_SUBNODE_FOR_LONG_IDS)) {
                    // 下一个节点不是当前节点的子节点。则当前节点是最子节点
                    resultList.add(customArray[i]);
                } else {
                    // 下一个节点是当前节点的子节点。则当前节点不是最子节点
                }
            }
            thisNodeLevel = postNodeLevel;
        }
        // 末尾节点一定是最子节点
        resultList.add(customArray[customArray.length - 1]);

        // 还原回原始分隔符
        if (!isSeparatorSame) {
            for (int i = 0; i < resultList.size(); i++) {
                resultList.set(i, resultList.get(i).replaceAll(SEPARATOR_IN_FILTER_SUBNODE_FOR_LONG_IDS, separator));
            }
        }

        return resultList;
    }
}
