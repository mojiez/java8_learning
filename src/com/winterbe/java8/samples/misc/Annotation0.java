package com.winterbe.java8.samples.misc;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;

public class Annotation0 {
    // Java语言使用@interface语法来定义注解（Annotation），它的格式如下：
    public @interface Report {
        int type() default 0;
        String level() default "info";
        String value() default "";
        // 注解的参数类似无参数方法， 可以用default设置一个默认值
    }

//    Annotation是一个接口
//    @Deprecated

}
