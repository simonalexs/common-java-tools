package io.github.simonalexs.annotation;


import io.github.simonalexs.Starter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 由该注解标记的方法，将自动成为公共可使用的方法（会自动注册到{@link Starter}中）
 */
@Target({ ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface Param {
    /**
     * 用户输入时给用户的提示信息
     * @return 值
     */
    String tip() default "";

    /**
     * 是否必填
     * @return 值
     */
    boolean require() default false;

    /**
     * 默认值
     * @return 值
     */
    String value() default "";
}
