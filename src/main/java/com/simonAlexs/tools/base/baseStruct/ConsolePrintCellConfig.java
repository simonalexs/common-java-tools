package com.simonAlexs.tools.base.baseStruct;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.function.Function;

/**
 * @ClassName: CellConfig
 * @Description: TODO-wcy
 * @Author: wcy
 * @Date: 2022/4/18 14:24
 * @Version: 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConsolePrintCellConfig {
    private Function<Object, String> formatter = obj -> String.format("%9s", obj.toString());
}
