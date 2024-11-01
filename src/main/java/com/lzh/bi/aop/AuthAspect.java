package com.lzh.bi.aop;

import com.lzh.bi.enums.ErrorCode;
import com.lzh.bi.enums.RoleEnum;
import com.lzh.bi.exception.BusinessException;
import com.lzh.bi.pojo.vo.UserVo;
import com.lzh.bi.service.UserService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 自定义切面类
 *
 * @author by
 */
@Aspect
@Component
public class AuthAspect {

    @Resource
    private UserService userService;

    /**
     * 校验管理员权限
     *
     * @param joinPoint 连接点
     */
    @Around("@annotation(com.lzh.bi.annotation.MustAdmin)")
    public Object doAuth(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取请求对象
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();

        // 获取用户登录态
        UserVo userVo = userService.getLoginUser(request);

        // 如果不是管理员，则抛出异常
        if (!RoleEnum.ADMIN.getCode().equals(userVo.getUserRole())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }

        // 放行
        return joinPoint.proceed();
    }
}
