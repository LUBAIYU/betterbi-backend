package com.lzh.bi.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * 登录校验切面类、统一判断用户是否登录
 *
 * @author by
 */
@Aspect
@Component
public class LoginAspect {
    @Around("@annotation(com.lzh.bi.annotation.LoginCheck)")
    public Object checkLogin(ProceedingJoinPoint joinPoint) throws Throwable {
        return null;
    }
}
