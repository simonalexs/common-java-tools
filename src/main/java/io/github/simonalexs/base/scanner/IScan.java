package io.github.simonalexs.base.scanner;

import java.util.List;
import java.util.function.Predicate;

public interface IScan {
    String CLASS_SUFFIX = ".class";

    List<Class<?>> search(String packageName, Predicate<Class<?>> predicate);

    default List<Class<?>> search(String packageName){
        return search(packageName,null);
    }
}