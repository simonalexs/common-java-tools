package io.github.simonalexs.tools.algorithm;


import java.util.*;

/**
 * @ClassName: OtherAlgorithm
 * @Description: TODO-wcy
 * @Author: wcy
 * @Date: 2022/6/20 09:48
 * @Version: 1.0
 */
public class LeetCodeQuestions {

    /**
     * 1723. 完成所有工作的最短时间
     * https://leetcode.cn/problems/find-minimum-time-to-finish-all-jobs
     * @param jobs 需要做的工作耗时列表 1--10^7
     * @param k 能分给的工人数量 1--12
     * @return 最小 的 最大工作时间
     */
    public static long q1723(int[] jobs, int k) {
        if (jobs.length <= k) {
            return Arrays.stream(jobs).max().getAsInt();
        }
        Arrays.sort(jobs);
        Map<String, Object>[] assignedJobs = new HashMap[k];
        for (int i = 0; i < assignedJobs.length; i++) {
            final HashMap<String, Object> assignedJobMap = new HashMap<>();
            assignedJobMap.put("jobs", new ArrayList<Integer>());
            assignedJobMap.put("times", 0);
            assignedJobMap.put("index", i);
            assignedJobs[i] = assignedJobMap;
        }
        for (int i = 0; i < jobs.length; i++) {
            final Map<String, Object> stringObjectMap = Arrays.stream(assignedJobs)
                    .min(Comparator.comparing(map -> Integer.valueOf(map.get("times").toString())))
                    .get();
            final Integer index = Integer.valueOf(stringObjectMap.get("index").toString());
            assignedJobs[index].put("times", Long.valueOf(assignedJobs[index].get("times").toString()) + jobs[i]);
            ((ArrayList<Integer>) assignedJobs[index].get("jobs")).add(jobs[i]);
        }
        final Map<String, Object> times = Arrays.stream(assignedJobs).max(Comparator.comparing(map -> Integer.valueOf(map.get("times").toString()))).get();
        return Long.valueOf(times.get("times").toString());
    }
}
