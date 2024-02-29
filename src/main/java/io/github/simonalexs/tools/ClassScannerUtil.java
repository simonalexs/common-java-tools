package io.github.simonalexs.tools;

import io.github.simonalexs.base.scanner.ScanExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class ClassScannerUtil {
    public static List<Class<?>> searchClasses(String packageName){
        return searchClasses(packageName, (Predicate<Class<?>>) null);
    }

    public static<T> List<Class<T>> searchClasses(String packageName, Class<T> clazz){
        List<Class<?>> classes = ScanExecutor.getInstance().search(packageName, c -> clazz.isAssignableFrom(c) && c != clazz);
        List<Class<T>> result = new ArrayList<>();
        for (Class<?> aClass : classes) {
            result.add((Class<T>) aClass);
        }
        return result;
    }

    public static List<Class<?>> searchClasses(String packageName, Predicate<Class<?>> predicate){
        return ScanExecutor.getInstance().search(packageName, predicate);
    }
}
