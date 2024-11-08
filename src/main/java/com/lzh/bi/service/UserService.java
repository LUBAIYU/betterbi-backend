package com.lzh.bi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lzh.bi.pojo.dto.UserLoginDto;
import com.lzh.bi.pojo.dto.UserPageDto;
import com.lzh.bi.pojo.dto.UserRegisterDto;
import com.lzh.bi.pojo.dto.UserUpdateDto;
import com.lzh.bi.pojo.entity.User;
import com.lzh.bi.pojo.vo.UserVo;
import com.lzh.bi.utils.PageBean;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

    /**
     * 上传图片（头像）
     *
     * @param multipartFile 文件对象
     * @return 图片地址
     */
    String uploadAvatar(MultipartFile multipartFile);

    /**
     * 获取图片
     *
     * @param fileName 图片名称
     * @param response 响应对象
     */
    void getAvatar(String fileName, HttpServletResponse response);
}
