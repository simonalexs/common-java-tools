package io.github.simonalexs.tools;

import io.github.simonalexs.annotation.Func;
import io.github.simonalexs.annotation.Param;
import io.github.simonalexs.tools.other.PrintUtil;

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

    public static void checkAndAutoStartAppPeriodically(String pathOfApp) throws IOException {
        checkAndAutoStartAppPeriodically(pathOfApp, 2, 3);
    }

    public static void checkAndAutoStartAppPeriodically(String pathOfApp, int waitingSecondForStart,
                                                        int checkPeriodSecond) throws IOException {
        checkAndAutoStartAppPeriodically(pathOfApp, waitingSecondForStart, checkPeriodSecond, Integer.MAX_VALUE);
    }

    @Func
    public static void checkAndAutoStartAppPeriodically(
            String pathOfApp,
            @Param("2") int waitingSecondForStart,
            @Param("3") int checkPeriodSecond,
            @Param("2147483647") int wholeRunSecondsOfTool) {
        Runnable runnable = () -> {
            try {
                if (!findProcess(pathOfApp)) {
                    if (!startApp(pathOfApp, waitingSecondForStart)) {
                        PrintUtil.println("启动失败");
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
        ScheduleUtil.runPeriodically(runnable, checkPeriodSecond * 1000, wholeRunSecondsOfTool * 1000);
    }

    /**
     * 传入启动应用路径，运行open命令
     * @param pathOfApp 应用路径
     * @param waitingSecondForStart 等待启动的时间
     * @return 是否启动成功
     * @throws Exception 异常
     */
    public static boolean startApp(String pathOfApp, int waitingSecondForStart) throws Exception {
        Runtime.getRuntime().exec("cmd /c " + dealPath(pathOfApp));
        Thread.sleep(waitingSecondForStart * 1000L);
        boolean process = findProcess(pathOfApp);
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
     * @param processNameOrPathOfApp 进程路径或名称
     * @return 是否找到进程
     * @throws IOException 异常
     */
    public static boolean findProcess(String processNameOrPathOfApp) throws IOException {
        if (processNameOrPathOfApp == null || processNameOrPathOfApp.isEmpty()) {
            throw new IOException("process name must not be null or empty");
        }
        File file = new File(processNameOrPathOfApp);
        String realProcessName;
        if (file.isFile()) {
            realProcessName = file.getName();
        } else {
            realProcessName = processNameOrPathOfApp;
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
     * @param filePath 进程路径
     * @return 是否终止成功
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
