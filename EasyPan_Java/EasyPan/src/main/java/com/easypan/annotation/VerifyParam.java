package com.easypan.annotation;


import com.easypan.entity.enums.VerifyRegexEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * AOP校验参数注解
 *
 * @date 2024/7/17 19:07
 * @author LiMengYuan
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.FIELD})
public @interface VerifyParam {
    /**
     * 校验正则， 默认不校验
     */
    VerifyRegexEnum regex() default VerifyRegexEnum.NO;

    /**
     * 最小长度， 默认-1
     */
    int min() default -1;

    /**
     * 最大长度， 默认-1
     */
    int max() default -1;

    /**
     * 是否必传， 默认false
     */
    boolean required() default false;
}
