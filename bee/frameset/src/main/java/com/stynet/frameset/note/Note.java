package com.stynet.frameset.note;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by zhouda on 2018/5/3.
 * java 注解 基本原理 编程实现 https://www.cnblogs.com/jtlgb/p/6366054.html
 * Java注解编程指南 https://blog.csdn.net/river_like/article/details/21542669
 * 创建你自己的 Java 注解类 https://blog.csdn.net/hj7jay/article/details/53759032
 * Java自定义注解和运行时靠反射获取注解 https://blog.csdn.net/bao19901210/article/details/17201173/
 * java 自定义注解以及获得注解的值 https://www.cnblogs.com/mouseIT/p/5033746.html
 * java 类,变量,方法上注解值的获取 https://blog.csdn.net/qq_24551315/article/details/51227526
 */
@Retention(RetentionPolicy.SOURCE)//不能重复注释//注解仅存在于源码中，在class字节码文件中不包含
//@Retention(RetentionPolicy.CLASS)//默认的保留策略，注解会在class字节码文件中存在，但运行时无法获得，
//@Retention(RetentionPolicy.RUNTIME)// 注解会在class字节码文件中存在，在运行时可以通过反射获取到
//@Retention(RetentionPolicy.RUNTIME)// 注解会在class字节码文件中存在，在运行时可以通过反射获取到
@Target({ElementType.FIELD, ElementType.METHOD})//定义注解的作用目标**作用范围字段、枚举的常量/方法
@Documented//说明该注解将被包含在javadoc中
public @interface Note {
    boolean out()default true;
}
