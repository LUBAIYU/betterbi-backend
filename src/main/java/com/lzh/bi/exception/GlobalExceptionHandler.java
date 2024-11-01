package com.lzh.bi.exception;

import com.lzh.bi.enums.ErrorCode;
import com.lzh.bi.utils.Result;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

/**
 * 自定义异常处理类
 *
 * @author by
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public static <T> Result<T> error(BusinessException be) {
        return Result.error(be.getCode(), be.getMessage());
    }

    /**
     * 处理请求参数校验异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public static <T> Result<T> handleValidException(MethodArgumentNotValidException me) {
        List<ObjectError> allErrors = me.getBindingResult().getAllErrors();
        StringBuilder errorMsg = new StringBuilder();
        for (int i = 0; i < allErrors.size(); i++) {
            ObjectError error = allErrors.get(i);
            errorMsg.append(error.getDefaultMessage());
            if (i != allErrors.size() - 1) {
                errorMsg.append(",");
            }
        }
        return Result.error(ErrorCode.PARAMS_ERROR.getCode(), errorMsg.toString());
    }
}
