package com.simonalexs.tools;

import com.simonalexs.tools.other.PrintUtil;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class WinUtil {

    public static void copy(String content) {
        Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable tText = new StringSelection(content);
        clip.setContents(tText, null);
    }

    public static void autoStartAppCycle(String filePath) throws IOException {
        autoStartAppCycle(filePath, 2, 3);
    }

    public static void autoStartAppCycle(String filePath, int waitSecondForStart,
                                         int checkPeriodSecond) throws IOException {
        autoStartAppCycle(filePath, waitSecondForStart, checkPeriodSecond, Integer.MAX_VALUE);
    }

    public static void autoStartAppCycle(String filePath, int waitSecondForStart,
                                         int checkPeriodSecond, int runningSeconds) throws IOException {
        long endTime = System.currentTimeMillis() + runningSeconds * 1000L;
        Runnable runnable = () -> {
            if (System.currentTimeMillis() >= endTime) {
                throw new RuntimeException("时间到，任务停止");
            }
            try {
                if (!findProcess(filePath)) {
                    if (!startApp(filePath, waitSecondForStart)) {
                        PrintUtil.println("启动失败");
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };

        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(runnable, 2, checkPeriodSecond, TimeUnit.SECONDS);
    }

    /**
     * 传入启动应用路径，运行open命令
     */
    public static boolean startApp(String filePath, int waitSecondAfterStart) throws Exception {
        Runtime.getRuntime().exec("cmd /c " + dealPath(filePath));
        Thread.sleep(waitSecondAfterStart * 1000L);
        boolean process = findProcess(filePath);
        if (process) {
            PrintUtil.println("启动执行完成");
        } else {
            PrintUtil.println("启动失败");
        }
        return process;
    }

    private static String dealPath(String filePath) {
        return "\"" + filePath + "\"";
    }

    /**
     * 传入进程名称processName,判断是进程是否存在
     */
    public static boolean findProcess(String processNameOrPath) throws IOException {
        File file = new File(processNameOrPath);
        String realProcessName;
        if (file.isFile()) {
            realProcessName = file.getName();
        } else {
            realProcessName = processNameOrPath;
        }
        BufferedReader bufferedReader = null;
        try {
            String command = "tasklist -fi " + '"' + "imagename eq " + realProcessName + '"';
            Process proc =
                    Runtime.getRuntime().exec(command);
            bufferedReader = new BufferedReader(new InputStreamReader(proc.getInputStream(), "GBK"));
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.contains(realProcessName)) {
                    PrintUtil.println("成功找到进程");
                    return true;
                }
            }
            PrintUtil.println("未找到进程");
            return false;
        } catch (Exception ex) {
            PrintUtil.println("未找到进程");
            return false;
        } finally {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
        }
    }

    /**
     * 传入进程名称，关闭进程
     */
    public static boolean killProcess(String filePath) {
        BufferedReader brStd = null;
        BufferedReader brErr = null;
        try {
            if (filePath != null) {
                Process pro = Runtime.getRuntime().exec("c:\\windows\\system32\\taskkill /F /im " + dealPath(filePath));
                brStd = new BufferedReader(new InputStreamReader(pro.getInputStream()));
                brErr = new BufferedReader(new InputStreamReader(pro.getErrorStream()));
                long time = System.currentTimeMillis();
                while (true) {
                    if (brStd.ready()) {
                        break;
                    }
                    if (brErr.ready()) {
                        break;
                    }
                    if (System.currentTimeMillis() - time > 3000) {
                        return false;
                    }
                }
            }
        } catch (IOException e1) {
            throw new RuntimeException(e1.getMessage());
        } finally {
            //关闭流
            try {
                if (brErr != null) {
                    brErr.close();
                }
            } catch (IOException ex) {
                throw new RuntimeException("释放资源失败");
            } finally {
                try {
                    if (brStd != null) {
                        brStd.close();
                    }
                } catch (IOException ex) {
                    throw new RuntimeException("释放资源失败");
                }
            }
        }
        return true;
    }
}
