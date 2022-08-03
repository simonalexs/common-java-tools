package com.simonAlexs.tools.test.interfaceUtil;

import com.simonAlexs.tools.interfaceUtil.FieldGetter;
import com.simonAlexs.tools.test.algorithm.A;

public class TestFieldGetter {

    public static void main(String[] args) {

        System.out.println(getFieldName(A::getA2));

    }

    public static <T, R> String getFieldName(FieldGetter<T, R> fieldGetter) {

        return fieldGetter.getFieldName();

    }
}