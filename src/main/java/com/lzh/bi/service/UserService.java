package com.lzh.bi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lzh.bi.pojo.dto.UserLoginDto;
import com.lzh.bi.pojo.dto.UserPageDto;
import com.lzh.bi.pojo.dto.UserRegisterDto;
import com.lzh.bi.pojo.dto.UserUpdateDto;
import com.lzh.bi.pojo.entity.User;
import com.lzh.bi.pojo.vo.UserVo;
import com.lzh.bi.utils.PageBean;

import javax.servlet.http.HttpServletRequest;

/**
 * @author lzh
 */
public interface UserService extends IService<User> {

    /**
     * 用户登录
     *
     * @param dto     登录参数
     * @param request 请求
     * @return 登录用户信息
     */
    UserVo userLogin(UserLoginDto dto, HttpServletRequest request);

    /**
     * 用户注册
     *
     * @param dto 注册参数
     * @return 用户ID
     */
    long userRegister(UserRegisterDto dto);

    /**
     * 根据ID查询用户信息
     *
     * @param id      用户ID
     * @param request 请求对象
     * @return 用户信息
     */
    User getUserInfoById(Long id, HttpServletRequest request);

    /**
     * 获取登录用户信息
     *
     * @param request 请求对象
     * @return 用户信息
     */
    UserVo getLoginUser(HttpServletRequest request);

    /**
     * 分页查询用户信息
     *
     * @param dto 查询参数
     * @return 用户信息列表
     */
    PageBean<User> pageUsers(UserPageDto dto);

    /**
     * 根据ID修改用户信息
     *
     * @param dto 用户信息参数
     * @return 是否修改成功
     */
    boolean updateUserById(UserUpdateDto dto, HttpServletRequest request);
}
