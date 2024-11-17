package com.lzh.bi.controller;

import cn.hutool.core.util.StrUtil;
import com.lzh.bi.annotation.LoginCheck;
import com.lzh.bi.annotation.MustAdmin;
import com.lzh.bi.constants.UserConst;
import com.lzh.bi.enums.ErrorCode;
import com.lzh.bi.exception.BusinessException;
import com.lzh.bi.pojo.dto.UserLoginDto;
import com.lzh.bi.pojo.dto.UserPageDto;
import com.lzh.bi.pojo.dto.UserRegisterDto;
import com.lzh.bi.pojo.dto.UserUpdateDto;
import com.lzh.bi.pojo.entity.User;
import com.lzh.bi.pojo.vo.UserVo;
import com.lzh.bi.service.UserService;
import com.lzh.bi.utils.PageBean;
import com.lzh.bi.utils.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * @author lzh
 */
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserService userService;

    @PostMapping("/login")
    public Result<UserVo> userLogin(@Valid @RequestBody UserLoginDto dto, HttpServletRequest request) {
        return Result.success(userService.userLogin(dto, request));
    }

    @PostMapping("/register")
    public Result<Long> userRegister(@Valid @RequestBody UserRegisterDto dto) {
        return Result.success(userService.userRegister(dto));
    }

    @LoginCheck
    @GetMapping("/get/{id}")
    public Result<User> getUserInfoById(@PathVariable @NotNull Long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return Result.success(userService.getUserInfoById(id, request));
    }

    @LoginCheck
    @DeleteMapping("/delete/{id}")
    public Result<Boolean> deleteUserById(@PathVariable @NotNull Long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return Result.success(userService.removeById(id));
    }

    @MustAdmin
    @PostMapping("/page")
    public Result<PageBean<User>> listUserByPage(@RequestBody @Valid UserPageDto dto) {
        return Result.success(userService.pageUsers(dto));
    }

    @LoginCheck
    @PutMapping("/update")
    public Result<Boolean> updateUserById(@RequestBody @Valid UserUpdateDto dto, HttpServletRequest request) {
        return Result.success(userService.updateUserById(dto, request));
    }

    @LoginCheck
    @PostMapping("/upload/avatar")
    public Result<String> uploadAvatar(MultipartFile multipartFile) {
        if (multipartFile == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return Result.success(userService.uploadAvatar(multipartFile));
    }

    @LoginCheck
    @GetMapping("/get/avatar/{fileName}")
    public void getAvatar(@PathVariable String fileName, HttpServletResponse response) {
        if (StrUtil.isBlank(fileName) || response == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        userService.getAvatar(fileName, response);
    }

    @GetMapping("/getLoginUser")
    public Result<UserVo> getLoginUser(HttpServletRequest request) {
        return Result.success(userService.getLoginUser(request));
    }

    @LoginCheck
    @PostMapping("/logout")
    public Result<Void> userLogout(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        request.getSession().setAttribute(UserConst.USER_LOGIN_STATE, null);
        return Result.success();
    }
}
