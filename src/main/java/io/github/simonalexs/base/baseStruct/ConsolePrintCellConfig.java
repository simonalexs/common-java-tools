package io.github.simonalexs.base.baseStruct;


import java.util.function.Function;

/**
 * ConsolePrintCellConfig
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
