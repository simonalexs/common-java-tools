package io.github.simonalexs.tools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class CollectionUtil {
    public static Object[] combineArray(Object... others) {
        return others;
    }
//    public static Object[] combineArray(Object obj, Object... others) {
//        return combineArray(new Object[]{obj}, others);
//    }
//
//    public static Object[] combineArray(Object[] objs, Object... others) {
//        List<Object> list1 = Arrays.stream(objs).collect(Collectors.toList());
//        List<Object> list2 = Arrays.stream(others).collect(Collectors.toList());
//        return combineCollection(list1, list2).toArray();
//    }

    public static <T> List<T> combineCollection(Collection<T> c1, Collection<T> c2) {
        return combineCollection(Arrays.asList(c1, c2));
    }

    public static <T> List<T> combineCollection(Collection<T> c1, Collection<T> c2, Collection<T> c3) {
        return combineCollection(Arrays.asList(c1, c2, c3));
    }

    public static <T> List<T> combineCollection(List<Collection<T>> c) {
        List<T> list = new ArrayList<>();
        for (Collection<T> collection : c) {
            list.addAll(collection);
        }
        return list;
    }
}
