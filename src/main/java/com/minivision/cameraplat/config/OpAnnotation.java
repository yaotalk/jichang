package com.minivision.cameraplat.config;

import java.lang.annotation.*;

/**
 * Created by yao on 2017/3/12.
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD,ElementType.TYPE})
@Documented
public @interface OpAnnotation {

    String modelName() default "";

    String opration() default "";

}
