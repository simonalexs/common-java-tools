package io.github.simonalexs.base.scanner;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public class ScanExecutor implements IScan {
    private static final List<IScan> SCANNER_LIST = new ArrayList<>();

    static {
        IScan jarScanner = new JarScanner();
        List<Class<?>> jarSearch = jarScanner.search(ScanExecutor.class.getPackage().getName(),
                clazz -> IScan.class.isAssignableFrom(clazz) && clazz != ScanExecutor.class && clazz != IScan.class);
        try {
            for (Class<?> aClass : jarSearch) {
                IScan iScan = (IScan) aClass.getConstructor().newInstance();
                SCANNER_LIST.add(iScan);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Class<?>> search(String packageName, Predicate<Class<?>> predicate) {
        Set<Class<?>> set = new HashSet<>();
        for (IScan iScan : SCANNER_LIST) {
            List<Class<?>> search = iScan.search(packageName, predicate);
            set.addAll(search);
        }
        return new ArrayList<>(set);
    }

    public static ScanExecutor getInstance(){
        return new ScanExecutor();
    }
}