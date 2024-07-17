package com.easypan.annotation;

import org.springframework.web.bind.annotation.Mapping;

import java.lang.annotation.*;

/**
 * AOP注解定义
 *
 * @date 2024/7/17 19:05
 * @author LiMengYuan
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Mapping
public @interface GlobalInterceptor {

    /**
     * 校验登录
     *
     * @return
     */
    boolean checkLogin() default true;

    /**
     * 校验参数 默认false
     */
    boolean checkParams() default false;

    /**
     * 校验管理员
     *
     * @return
     */
    boolean checkAdmin() default false;
}
