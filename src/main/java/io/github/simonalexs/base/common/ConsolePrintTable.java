package io.github.simonalexs.base.common;

import io.github.simonalexs.tools.other.PrintUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

/**
 * ConsolePrintTable
 */
public class ConsolePrintTable {
    private String tableSeparator = "~";
    private String titleSeparator = "=";
    private String rowSeparator = "-";
    private String colSeparator = "    ";
    private int rowLength = 0;
    private String tableTitle = "";
    private List<String> colTitles = new ArrayList<>();
    private List<List<?>> datas = new ArrayList<>();

    public static ConsolePrintTable getInstance(){
        return new ConsolePrintTable("");
    }
    public static ConsolePrintTable getInstance(String tableTitle){
        return new ConsolePrintTable(tableTitle);
    }

    private ConsolePrintTable(String tableTitle) {
        this.tableTitle = tableTitle;
    }

    public ConsolePrintTable.Builder getBuilder(){
        return new Builder(this);
    }

    public String prettyPrint() {
        Pair<List<Integer>, String> columnWidthConfigPair = PrintUtil.getColumnWidthConfig(colTitles, datas);
        int rowMaxWidth = columnWidthConfigPair.getRight().length();

        StringBuilder result = new StringBuilder();
        // 添加标题
        String tableTitleRowStr =
                StringUtils.repeat(tableSeparator, rowMaxWidth / 3)
                        + tableTitle
                        + StringUtils.repeat(tableSeparator, rowMaxWidth - rowMaxWidth / 3 - tableTitle.length());
        result.append(tableTitleRowStr);
        result.append("\n");

        String colTitleStr = PrintUtil.generateRowStr(colTitles, columnWidthConfigPair.getLeft());
        result.append(colTitleStr);
        result.append("\n");
        result.append(StringUtils.repeat(titleSeparator, rowMaxWidth));
        result.append("\n");
        for (List<?> data : datas) {
            String rowStr = PrintUtil.generateRowStr(data, columnWidthConfigPair.getLeft());
            result.append(rowStr);
            result.append("\n");
        }
        return result.toString();
    }

    public static class Builder {
        private final ConsolePrintTable consolePrintTable;

        public Builder(ConsolePrintTable consolePrintTable){
            this.consolePrintTable = consolePrintTable;
        }

        public Builder addTitle(String title){
            consolePrintTable.colTitles.add(title);
            return this;
        }

        public Builder addTitle(List<String> title){
            consolePrintTable.colTitles.addAll(title);
            return this;
        }

        public Builder addRowData(List<Object> rowData){
            consolePrintTable.datas.add(rowData);
            return this;
        }

        public ConsolePrintTable build(){
            return consolePrintTable;
        }
    }
}
