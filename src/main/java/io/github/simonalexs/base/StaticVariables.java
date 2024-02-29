package io.github.simonalexs.base;

import java.lang.reflect.Field;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

public class StaticVariables {
    public static final Map<Integer, String> SQL_TYPES = new HashMap<>();
    static {
        try {
            Field[] fields = Types.class.getDeclaredFields();
            for (Field field : fields) {
                if (field.getType() == int.class) {
                    SQL_TYPES.put((int) field.get(null), field.getName());
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
