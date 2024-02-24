package com.simonalexs;

public class Starter {
    public static void main(String[] args) {
        // 1.输出 help 信息

        // 【可提供两种运行模式：
        //       一种是带命令参数运行特定工具
        //       一种是交互式运行，逐步选择，运行最后要记得打印出“本次运行的完整参数列表，便于用户复制粘贴”】

        System.out.println("welcome simon alexs tools");
        System.out.println("args.length = " + args.length);
        for (int i = 0; i < args.length; i++) {
            System.out.println("args[i] = " + args[i]);
        }
    }
}
