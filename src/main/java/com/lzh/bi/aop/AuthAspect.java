package com.lzh.bi.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * 自定义切面类
 *
 * @author by
 */
@Aspect
@Component
public class AuthAspect {

    /**
     * 校验管理员权限
     *
     * @param joinPoint 连接点
     */
    @Around("@annotation(com.lzh.bi.annotation.MustAdmin)")
    public Object doAuth(ProceedingJoinPoint joinPoint) throws Throwable {
        return null;
    }
}
