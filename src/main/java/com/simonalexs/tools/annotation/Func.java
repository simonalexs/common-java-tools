package com.simonalexs.tools.annotation;


import com.simonalexs.Starter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 由该注解标记的方法，将自动成为公共可使用的方法（会自动注册到{@link Starter}中）
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Func {
    /**
     * 描述信息（方法功能描述）
     */
    String value() default "";
}
