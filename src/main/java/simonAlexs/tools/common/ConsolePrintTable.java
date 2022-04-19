package simonAlexs.tools.common;

import lombok.Data;
import simonAlexs.tools.baseStruct.ConsolePrintCellConfig;

import java.util.*;

/**
 * @ClassName: ConsolePrintTable
 * @Description: TODO-wcy
 * @Author: wcy
 * @Date: 2022/4/18 11:42
 * @Version: 1.0
 */
@Data
public class ConsolePrintTable {
    private String tableSeparator = "~";
    private String titleSeparator = "=";
    private String rowSeparator = "-";
    private String colSeparator = "    ";
    private int rowLength = 0;
    private Map<String, ConsolePrintCellConfig> titleConfigs = new LinkedHashMap<>();
    private List<Map<String, Object>> datas = new LinkedList<>();

    public static ConsolePrintTable getInstance(){
        return new ConsolePrintTable();
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
        String tableSeparatorStr = tableSeparator.repeat(rowLength);
        result.append(tableSeparatorStr);
        result.append("\n");
        result.append(titleResult);
        result.append("\n");
        result.append(titleSeparator.repeat(rowLength));
        result.append("\n");
        String rowSeparatorStr = rowSeparator.repeat(rowLength);
        for (int i = 0, len = datas.size(); i < len; i++) {
            for (Map.Entry<String, ConsolePrintCellConfig> entry : titleConfigs.entrySet()) {
                Object dataValue = datas.get(i).get(entry.getKey());
                result.append(entry.getValue().getFormatter().apply(dataValue));
                result.append(colSeparator);
            }
            if (i != len - 1) {
                result.append("\n");
                result.append(rowSeparatorStr);
            }
            result.append("\n");
        }
        result.append(tableSeparatorStr);
        result.append("\n");
        return result.toString();
    }

    public class Builder {
        private ConsolePrintTable consolePrintTable;

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
            Map<String, Object> rowData = new HashMap<>();

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
}
