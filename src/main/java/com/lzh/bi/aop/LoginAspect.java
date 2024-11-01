package com.lzh.bi.aop;

import com.lzh.bi.constants.UserConst;
import com.lzh.bi.enums.ErrorCode;
import com.lzh.bi.exception.BusinessException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

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
        // 获取请求对象
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        // 获取用户登录态
        Object object = request.getSession().getAttribute(UserConst.USER_LOGIN_STATE);
        // 判断是否有登录
        if (object == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        // 放行
        return joinPoint.proceed();
    }
}
