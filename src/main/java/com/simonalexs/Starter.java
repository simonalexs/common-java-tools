package com.simonalexs;

import com.simonalexs.handler.UtilHandler;
import com.simonalexs.tools.annotation.Func;
import com.simonalexs.tools.other.PrintUtil;

import java.io.Serializable;
import java.sql.ParameterMetaData;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Starter {
    private static final Page MAIN_PAGE = new MainPage(null);

    public static void main(String[] args) {
        // 1.输出 help 信息

        // 【可提供三种运行模式：
        //       一种是带命令参数运行特定工具
        //       一种是交互式运行，逐步选择，运行最后要记得打印出“本次运行的完整参数列表，便于用户复制粘贴”】
        //       一种是输出本包中所有工具类及其下的工具方法、参数、返回值（能每个方法带有说明信息的话更好），由用户自由选择使用哪一个

        Page page = MAIN_PAGE;
        while (page != null) {
            page = page.show();
        }
    }

    private static abstract class Page {
        private final Page previousPage;
        protected Map<Integer, Page> pageMap = new LinkedHashMap<>();
        public Page(Page previousPage) {
            this.previousPage = previousPage;
            resetPageMap();
        }

        protected String resetPageMap() {
            pageMap.clear();
            int indexToPrevious = -1;
            int indexToMain = 0;
            pageMap.put(indexToPrevious, previousPage);
            pageMap.put(indexToMain, MAIN_PAGE);
            return "(input " + indexToPrevious + " to back to previous page, " +
                    "input " + indexToMain + " to back to main page)";
        }

        /**
         * 打印出界面中的所有信息
         */
        public abstract Page show();
        protected abstract void showPageContent();
    }

    private static class MainPage extends Page {
        public MainPage(Page previousPage) {
            super(previousPage);
        }

        @Override
        public Page show() {
            Scanner input = new Scanner(System.in);
            while (true) {
                showPageContent();
                System.out.println("Please choose order:");
                try {
                    String inputStr = input.nextLine();
                    int inputOrder = Integer.parseInt(inputStr);
                    if (!pageMap.containsKey(inputOrder)) {
                        continue;
                    }
                    return pageMap.get(inputOrder);
                } catch (NumberFormatException ignored) {
                }
            }
        }

        @Override
        protected void showPageContent() {
            System.out.println();
            String pageTips = resetPageMap();
            System.out.println("                         Welcome to SimonAlexs tools!");
            List<String> titles = Arrays.asList(
                    "order",
                    "module",
                    "name",
                    "desc");
            List<List<Object>> dataList = new ArrayList<>();
            AtomicInteger order = new AtomicInteger(1);
            UtilHandler.getModule2FuncsMap().forEach((module, funcList) -> {
                for (UtilHandler.FuncInfo funcInfo : funcList) {
                    List<Object> data = Arrays.asList(
                            order.get(),
                            module,
                            funcInfo.name(),
                            funcInfo.desc
                    );
                    dataList.add(data);
                    pageMap.put(order.get(), new FuncPage(this, funcInfo));
                    order.set(order.get() + 1);
                }
            });
            PrintUtil.println("=", "-", "=", titles, dataList);
            System.out.println(pageTips);
        }
    }

    private static class FuncPage extends Page {
        protected final UtilHandler.FuncInfo funcInfo;

        public FuncPage(Page previousPage, UtilHandler.FuncInfo funcInfo) {
            super(previousPage);
            this.funcInfo = funcInfo;
        }

        @Override
        public Page show() {
            Scanner input = new Scanner(System.in);
            String runCommand = "y";
            String userTipAfterInput = "";
            while (true) {
                showPageContent();
                if (!userTipAfterInput.isEmpty()) {
                    System.out.println("****************************************");
                    System.out.println(userTipAfterInput);
                    System.out.println("****************************************");
                    userTipAfterInput = "";
                }
                System.out.println("Please update param value by input order and value if needed. " +
                        "Input '" + runCommand + "' to run:");
                try {
                    String inputStr = input.nextLine();
                    if (inputStr.equalsIgnoreCase(runCommand)) {
                        // 执行方法
                        Object[] paramValues = new Object[funcInfo.params.size()];
                        for (int i = 0; i < funcInfo.params.size(); i++) {
                            UtilHandler.ParamInfo param = funcInfo.params.get(i);
                            Object value = param.parser.apply(param.currentValue);
                            paramValues[i] = value;
                        }
                        Object invokedResult = funcInfo.func.invoke(null, paramValues);
                        System.out.print("running success, ");
                        Class<?> returnType = funcInfo.func.getReturnType();
                        if (returnType == Void.class) {
                            System.out.println("no result.");
                        } else {
                            System.out.println("result: ");
                            System.out.println(invokedResult);
                        }
                        return null;
                    } else {
                        String[] split = inputStr.split(" ");
                        if (split.length == 0) {
                            userTipAfterInput = "find nothing from input";
                            continue;
                        }
                        int paramOrder = Integer.parseInt(split[0]);
                        // 返回页面
                        if (pageMap.containsKey(paramOrder)) {
                            return pageMap.get(paramOrder);
                        }
                        if (paramOrder <= 0 || paramOrder >= funcInfo.params.size()) {
                            userTipAfterInput = "order is wrong";
                            continue;
                        }
                        // 修改参数值
                        funcInfo.params.get(paramOrder - 1).currentValue = split[1].trim();
                    }
                } catch (Exception e) {
                    userTipAfterInput = e.getClass().getName() + ": " + e.getMessage();
                }
            }
        }

        @Override
        protected void showPageContent() {
            System.out.println();
            String pageTips = resetPageMap();
            System.out.println("func info:");
            List<List<String>> funcContent = Arrays.asList(
                    Arrays.asList("    module", funcInfo.module()),
                    Arrays.asList("    name", funcInfo.name()),
                    Arrays.asList("    desc", funcInfo.desc)
            );
            PrintUtil.println(funcContent);

            System.out.println("param info:");
            List<String> titles = Arrays.asList(
                    "order",
                    "paramName",
                    "currentValue",
                    "desc"
            );
            List<List<Object>> paramContent = new ArrayList<>();
            for (int i = 0; i < funcInfo.params.size(); i++) {
                UtilHandler.ParamInfo paramInfo = funcInfo.params.get(i);
                List<Object> paramDescription = Arrays.asList(
                        i + 1,
                        paramInfo.name,
                        paramInfo.currentValue,
                        paramInfo.tip
                );
                paramContent.add(paramDescription);
            }
            PrintUtil.println("=", "-", "=", titles, paramContent);
            System.out.println(pageTips);
        }
    }
}
