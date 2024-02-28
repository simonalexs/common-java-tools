package io.github.simonalexs.tools.test.algorithm;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import io.github.simonalexs.tools.algorithm.TreeUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.util.*;

import static io.github.simonalexs.tools.other.PrintUtil.println;

/**
 * @author admin
 */
public class TreeUtilTest {
    static String SEP_STR = "@";

    public static void main(String[] args) throws Exception {
        println("******************************");
        int firstLength = 1,
                secondLength = 2,
                thirdLength = 110,
                runTimes = 10,
                arrayNums = 100;
        Map<Integer, JSONObject> map = new HashMap<>();
        println("原始长度：" + arrayNums);
        println("统计次数：" + runTimes);

        long mySortTimes = 0,
                myFilterTimes = 0,
                myAllTimes = 0,
                commonUseTimes = 0;
        for (int i = 0; i < runTimes; i++) {
            filterSubNode(i, map, arrayNums, firstLength, secondLength, thirdLength);
            myAllTimes += map.get(i).getLongValue("myAllTime");
            commonUseTimes += map.get(i).getLongValue("commonUseTime");
        }
        println("myAllTimes平均耗时：" + (myAllTimes / runTimes));
        println("commonUseTimes平均耗时：" + (commonUseTimes / runTimes));
        println("******************************");
//        StopWatchMulti.print();
//        StopWatchMulti.print();
    }

    private static void filterSubNode(int runTimes, Map<Integer, JSONObject> map, int selectedArrayNums, int firstLength, int secondLength, int thirdLength) {
        long startTimeAll = System.nanoTime();
        int alllength = firstLength + firstLength * secondLength + firstLength * secondLength * thirdLength;
        List<String> allQueryStructList = new ArrayList<String>();
        for (int first = firstLength - 1, index = 0; first >= 0; first--) {
            String firstId = SEP_STR + "firstfirstfirstfirst_" + (first + 1);
            allQueryStructList.add(firstId);
            for (int second = secondLength - 1; second >= 0; second--) {
                String secondId = firstId + SEP_STR + "secondsecondsecondsecond_" + (second + 1);
                allQueryStructList.add(secondId);
                for (int third = thirdLength - 1; third >= 0; third--) {
                    String thirdId = secondId + SEP_STR + "thirdthirdthirdthird_" + (third + 1);
                    allQueryStructList.add(thirdId);
                }
            }
        }

        String[] selectedQueryStructList = new String[selectedArrayNums];

        for (int i = 0; i < selectedArrayNums; i++) {
            Random random = new Random();
            int index = random.nextInt(allQueryStructList.size());
            selectedQueryStructList[i] = allQueryStructList.remove(index);
        }
        /*for (int i = 0; i < selectedArrayNums; i++) {
            selectedQueryStructList[i] = allQueryStructList.get(i);
        }*/
        List<String> selectedQueryStructListCommon = Arrays.asList(selectedQueryStructList);

        // 原始方案
        List<String> resultListCommon = Collections.synchronizedList(new ArrayList<>());
        long commonStartTime = System.nanoTime();
        selectedQueryStructListCommon.parallelStream().forEach(t -> {
            if (selectedQueryStructListCommon.parallelStream().noneMatch(s -> s.startsWith(t + SEP_STR))) {
                resultListCommon.add(t);
            }
        });
        long commonUseTime = (System.nanoTime() - commonStartTime) / 1000 / 1000; // ms



        // ******************* 算法：筛选树节点长名列表中的“最子节点” begin *******************
        long myStartTime = System.nanoTime();
        List<String> resultListMine = TreeUtil.filterSubNodeForLongIds(selectedQueryStructList, SEP_STR);
        long myAllTime = (System.nanoTime() - myStartTime) / 1000 / 1000; // ms

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("myAllTime", myAllTime);
        jsonObject.put("commonUseTime", commonUseTime);
        jsonObject.put("filteredLengthMine", resultListMine.size());
        jsonObject.put("filteredLengthCommon", resultListCommon.size());
        map.put(runTimes, jsonObject);

//        writeToFile("******************************\n遍历结束 " + Calendar.getInstance().getTime().toLocaleString() + "\n" + "原始列表: ",
//                Arrays.asList(selectedQueryStructList));

        println(JSON.toJSONString(map.get(runTimes)));
//        writeStringToFile(JSON.toJSONString(map.get(runTimes)));

        /*if (resultListMine.size() < resultListCommon.size()) {
            resultListCommon.removeAll(resultListMine);
            println("common比mine多的结果集：" + resultListCommon.size());
            writeToFile("common比mine多的结果集：" + resultListCommon.size(), resultListCommon);
        } else if (resultListMine.size() > resultListCommon.size()) {
            resultListMine.removeAll(resultListCommon);
            println("mine比common多的结果集：" + resultListMine.size());
            writeToFile("mine比common多的结果集：" + resultListMine.size(), resultListMine);
        } else {
            println("结果集一致" + resultListMine.size());
            writeStringToFile("结果集一致" + resultListMine.size() + "\n");
        }
        writeStringToFile("******************************\n运行结束 \n\n");
        System.out.println(((System.nanoTime() - startTimeAll) / 1000 / 1000));*/
    }

    private static void writeStringToFile(String text) {
        try {
            File file = new File("E:\\log\\log.txt");
            if(!file.exists()){
                file.createNewFile();
            }

            byte[] gbks = text.getBytes("gbk");

            FileOutputStream fileOutputStream = new FileOutputStream(file, true);
            fileOutputStream.write(gbks);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static void writeToFile(String title, List<String> list) {
        try {
            File file = new File("E:\\log\\log.txt");
            if(!file.exists()){
                file.createNewFile();
            }

            StringBuffer stringBuffer = new StringBuffer("");
            list.forEach(t -> {
                stringBuffer.append(t + "\n");
            });
            byte[] gbks = (title + "\n" + stringBuffer.toString() + "\n\n").getBytes("gbk");

            FileOutputStream fileOutputStream = new FileOutputStream(file, true);
            fileOutputStream.write(gbks);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
