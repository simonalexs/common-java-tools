package io.github.simonalexs;

import io.github.simonalexs.handler.UtilHandler;
import io.github.simonalexs.tools.other.PrintUtil;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Starter {
    private static final Page MAIN_PAGE = new MainPage(null);

    public static void main(String[] args) {
        if (args.length == 0) {
            // 交互式运行
            runByInteraction();
        } else {
            // 依据参数运行
            runByCommand(args);
        }
    }

    private static void runByInteraction() {
        Page page = MAIN_PAGE;
        while (page != null) {
            page = page.show();
        }
    }

    private static void runByCommand(String[] args) {
        String module = args[0];
        String funcName = args[1];

        Map<String, List<UtilHandler.FuncInfo>> module2FuncsMap = UtilHandler.getModule2FuncsMap();
        Optional<String> moduleOptional =
                module2FuncsMap.keySet().stream().filter(t -> t.equalsIgnoreCase(module)).findFirst();
        if (!moduleOptional.isPresent()) {
            System.out.println("[ERROR] module not exists: [" + module + "].Available values: ");
            System.out.println(String.join(", ", module2FuncsMap.keySet()));
            return;
        }
        List<UtilHandler.FuncInfo> funcInfoList = module2FuncsMap.get(moduleOptional.get());
        Optional<UtilHandler.FuncInfo> funcInfoOptional = funcInfoList.stream().filter(t -> t.name().equalsIgnoreCase(funcName)).findFirst();
        if (!funcInfoOptional.isPresent()) {
            System.out.println("[ERROR] func not exists: [" + funcName + "].Available values: ");
            System.out.println(funcInfoList.stream().map(UtilHandler.FuncInfo::name).collect(Collectors.joining(", ")));
            return;
        }
        UtilHandler.FuncInfo func = funcInfoOptional.get();
        int realParamCount = args.length - 2;
        if (realParamCount != func.params.size()) {
            System.out.println("[ERROR] func param num error: [" + realParamCount + "].See func info: ");
            func.show();
            return;
        }
        // 开始执行
        String errorMsg = func.run();
        if (!errorMsg.isEmpty()) {
            System.out.println(errorMsg);
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
                String inputStr = input.nextLine();
                if (inputStr.equalsIgnoreCase(runCommand)) {
                    // 执行方法
                    String errorMsg = funcInfo.run();
                    if (!errorMsg.isEmpty()) {
                        userTipAfterInput = errorMsg;
                        continue;
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
                        userTipAfterInput = "the input order [" + paramOrder + "] is wrong";
                        continue;
                    }
                    // 修改参数值
                    funcInfo.params.get(paramOrder - 1).currentValue = split[1].trim();
                }
            }
        }

        @Override
        protected void showPageContent() {
            String pageTips = resetPageMap();
            funcInfo.show();
            System.out.println(pageTips);
        }
    }
}
