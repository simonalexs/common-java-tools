package io.github.simonalexs.tools.test;

import io.github.simonalexs.tools.WinUtil;

import java.io.IOException;

public class WinUtilTest {
    public static void main(String[] args) {
        try {
            WinUtil.checkAndAutoStartAppPeriodically("D:\\Workspace\\SVN\\PSOdbc\\DBServer.64.exe");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
//        boolean process = WinUtil.findProcess("D:\\Workspace\\SVN\\PSOdbc\\DBServer.64.exe");
//        boolean process = WinUtil.findProcess("DBServer.64.exe");
//        System.out.println(process);
    }
}
