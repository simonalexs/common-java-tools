package com.simonalexs.tools.base.baseStruct;


import java.util.function.Function;

/**
 * @ClassName: CellConfig
 * @Description: TODO-wcy
 * @Author: wcy
 * @Date: 2022/4/18 14:24
 * @Version: 1.0
 */
public class ConsolePrintCellConfig {
    private Function<Object, String> formatter = obj -> String.format("%9s", obj.toString());

    public ConsolePrintCellConfig(Function<Object, String> formatter) {
        this.formatter = formatter;
    }

    public ConsolePrintCellConfig() {
    }

    public Function<Object, String> getFormatter() {
        return formatter;
    }

    public void setFormatter(Function<Object, String> formatter) {
        this.formatter = formatter;
    }
}
