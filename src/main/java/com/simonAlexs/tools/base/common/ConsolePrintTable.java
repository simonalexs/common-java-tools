package com.simonAlexs.tools.base.common;

import com.simonAlexs.tools.base.baseStruct.ConsolePrintCellConfig;

import java.util.*;

import static org.apache.commons.lang3.StringUtils.repeat;

/**
 * @ClassName: ConsolePrintTable
 * @Description: TODO-wcy
 * @Author: wcy
 * @Date: 2022/4/18 11:42
 * @Version: 1.0
 */
public class ConsolePrintTable {
    private String tableSeparator = "~";
    private String titleSeparator = "=";
    private String rowSeparator = "-";
    private String colSeparator = "    ";
    private int rowLength = 0;
    private String tableTitle = "";
    private Map<String, ConsolePrintCellConfig> titleConfigs = new LinkedHashMap<>();
    private List<Map<String, Object>> datas = new LinkedList<>();

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
        StringBuilder titleResult = new StringBuilder("");
        titleConfigs.forEach((title, config) -> {
            String formattedTitle = config.getFormatter().apply(title);
            titleResult.append(formattedTitle);
            titleResult.append(colSeparator);
            rowLength += formattedTitle.length() + colSeparator.length();
        });

        StringBuilder result = new StringBuilder("");
        String tableSeparatorStr = repeat(tableSeparator, rowLength);
//        result.append(tableSeparatorStr);
//        result.append("\n");
        // 添加标题
        String tableTitleStr =
                repeat(tableSeparator, rowLength / 3) + tableTitle + repeat(tableSeparator, rowLength - rowLength / 3 - tableTitle.length());
        result.append(tableTitleStr);
        result.append("\n");

        result.append(titleResult);
        result.append("\n");
        result.append(repeat(titleSeparator, rowLength));
        result.append("\n");
        String rowSeparatorStr = repeat(rowSeparator, rowLength);
        for (int i = 0, len = datas.size(); i < len; i++) {
            for (Map.Entry<String, ConsolePrintCellConfig> entry : titleConfigs.entrySet()) {
                Object dataValue = datas.get(i).get(entry.getKey());
                result.append(entry.getValue().getFormatter().apply(dataValue));
                result.append(colSeparator);
            }
//            if (i != len - 1) {
//                result.append("\n");
//                result.append(rowSeparatorStr);
//            }
            result.append("\n");
        }
//        result.append(tableSeparatorStr);
//        result.append("\n");
        return result.toString();
    }

    public static class Builder {
        private final ConsolePrintTable consolePrintTable;

        public Builder(ConsolePrintTable consolePrintTable){
            this.consolePrintTable = consolePrintTable;
        }

        public Builder addTitlesByDefaultConfig(String... titles){
            for (String title : titles) {
                consolePrintTable.getTitleConfigs().put(title, new ConsolePrintCellConfig());
            }
            return this;
        }

        public Builder addTitle(String title, ConsolePrintCellConfig consolePrintCellConfig){
            consolePrintTable.getTitleConfigs().put(title, consolePrintCellConfig);
            return this;
        }

        public RowDataBuilder getRowDataBuilder(){
            return new RowDataBuilder(this);
        }

        public Builder addRowData(Map<String, Object> rowData){
            consolePrintTable.datas.add(rowData);
            return this;
        }

        public ConsolePrintTable build(){
            return consolePrintTable;
        }

        public class RowDataBuilder {
            Builder builder;
            public Map<String, Object> rowData = new HashMap<>();

            public RowDataBuilder(Builder builder) {
                this.builder = builder;
            }

            public RowDataBuilder addData(String title, Object data){
                rowData.put(title, data);
                return this;
            }

            public Map<String, Object> build(){
                return rowData;
            }
        }
    }

    public String getTableSeparator() {
        return tableSeparator;
    }

    public void setTableSeparator(String tableSeparator) {
        this.tableSeparator = tableSeparator;
    }

    public String getTitleSeparator() {
        return titleSeparator;
    }

    public void setTitleSeparator(String titleSeparator) {
        this.titleSeparator = titleSeparator;
    }

    public String getRowSeparator() {
        return rowSeparator;
    }

    public void setRowSeparator(String rowSeparator) {
        this.rowSeparator = rowSeparator;
    }

    public String getColSeparator() {
        return colSeparator;
    }

    public void setColSeparator(String colSeparator) {
        this.colSeparator = colSeparator;
    }

    public int getRowLength() {
        return rowLength;
    }

    public void setRowLength(int rowLength) {
        this.rowLength = rowLength;
    }

    public String getTableTitle() {
        return tableTitle;
    }

    public void setTableTitle(String tableTitle) {
        this.tableTitle = tableTitle;
    }

    public Map<String, ConsolePrintCellConfig> getTitleConfigs() {
        return titleConfigs;
    }

    public void setTitleConfigs(Map<String, ConsolePrintCellConfig> titleConfigs) {
        this.titleConfigs = titleConfigs;
    }

    public List<Map<String, Object>> getDatas() {
        return datas;
    }

    public void setDatas(List<Map<String, Object>> datas) {
        this.datas = datas;
    }
}
