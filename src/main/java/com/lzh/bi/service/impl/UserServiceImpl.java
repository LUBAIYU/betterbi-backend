package com.lzh.bi.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lzh.bi.constants.UserConst;
import com.lzh.bi.enums.ErrorCode;
import com.lzh.bi.enums.RoleEnum;
import com.lzh.bi.exception.BusinessException;
import com.lzh.bi.mapper.UserMapper;
import com.lzh.bi.pojo.dto.UserLoginDto;
import com.lzh.bi.pojo.dto.UserPageDto;
import com.lzh.bi.pojo.dto.UserRegisterDto;
import com.lzh.bi.pojo.dto.UserUpdateDto;
import com.lzh.bi.pojo.entity.User;
import com.lzh.bi.pojo.vo.UserVo;
import com.lzh.bi.service.UserService;
import com.lzh.bi.utils.PageBean;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

/**
 * @author lzh
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Override
    public UserVo userLogin(UserLoginDto dto, HttpServletRequest request) {
        String userAccount = dto.getUserAccount();
        String userPassword = dto.getUserPassword();

        // 判断用户是否存在
        User dbUser = this.lambdaQuery()
                .eq(User::getUserAccount, userAccount)
                .one();
        if (dbUser == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号或密码错误");
        }

        // 对密码进行md5加密，然后再校验
        String encryptPassword = DigestUtil.md5Hex(userPassword);
        if (!encryptPassword.equals(dbUser.getUserPassword())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号或密码错误");
        }

        // 用户信息脱敏
        UserVo userVo = new UserVo();
        BeanUtil.copyProperties(dbUser, userVo);

        // 保存用户信息到session中
        request.getSession().setAttribute(UserConst.USER_LOGIN_STATE, userVo);

        // 返回脱敏对象
        return userVo;
    }

    @Override
    public long userRegister(UserRegisterDto dto) {
        String userAccount = dto.getUserAccount();
        String userPassword = dto.getUserPassword();
        String checkPassword = dto.getCheckPassword();

        // 判断用户账号是否存在
        User dbUser = this.lambdaQuery()
                .eq(User::getUserAccount, userAccount)
                .one();
        if (dbUser != null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号已存在");
        }

        // 判断密码和确认密码是否一致
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码不一致");
        }

        // 对密码进行md5加密
        String encryptPassword = DigestUtil.md5Hex(userPassword);

        // 保存用户信息
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setUserRole(RoleEnum.USER.getCode());
        this.save(user);

        return user.getId();
    }

    @Override
    public User getUserInfoById(Long id, HttpServletRequest request) {
        // 获取当前的登录用户信息
        UserVo userVo = this.getLoginUser(request);

        // 如果当前用户不是管理员或者不是用户自己本身，则不能查看该用户信息
        Integer userRole = userVo.getUserRole();
        Long loginUserId = userVo.getId();
        if (!RoleEnum.ADMIN.getCode().equals(userRole) && !id.equals(loginUserId)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }

        // 返回用户信息
        return this.getById(id);
    }

    @Override
    public UserVo getLoginUser(HttpServletRequest request) {
        // 获取用户登录态
        Object object = request.getSession().getAttribute(UserConst.USER_LOGIN_STATE);
        UserVo userVo = (UserVo) object;
        if (userVo == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return userVo;
    }

    @Override
    public PageBean<User> pageUsers(UserPageDto dto) {
        // 分页参数
        IPage<User> pageParam = new Page<>(dto.getCurrent(), dto.getPageSize());

        // 查询参数
        String userAccount = dto.getUserAccount();
        String userName = dto.getUserName();
        Integer userRole = dto.getUserRole();

        // 查询
        IPage<User> result = this.lambdaQuery()
                .like(StrUtil.isNotBlank(userAccount), User::getUserAccount, userAccount)
                .like(StrUtil.isNotBlank(userName), User::getUserName, userName)
                .eq(userRole != null, User::getUserRole, userRole)
                .orderByDesc(User::getCreateTime)
                .page(pageParam);

        // 返回数据
        return PageBean.of(result.getTotal(), result.getRecords());
    }

    @Override
    public boolean updateUserById(UserUpdateDto dto, HttpServletRequest request) {
        // 获取登录用户信息
        UserVo userVo = this.getLoginUser(request);

        // 如果当前用户是普通用户，则不能修改角色状态
        Integer userCode = RoleEnum.USER.getCode();
        Integer adminCode = RoleEnum.ADMIN.getCode();
        if (userCode.equals(userVo.getUserRole()) && adminCode.equals(dto.getUserRole())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }

        // 对密码进行重新加密再修改
        String encryptPassword = DigestUtil.md5Hex(dto.getUserPassword());
        User user = new User();
        BeanUtil.copyProperties(dto, user);
        user.setUserPassword(encryptPassword);

        // 修改用户信息
        return this.updateById(user);
    }
}




