package com.lzh.bi.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.IoUtil;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

/**
 * @author lzh
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Value("${betterbi.server.path.domain}")
    private String domain;

    @Value("${betterbi.server.path.address}")
    private String address;

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
        String loginUserId = userVo.getId();
        if (!RoleEnum.ADMIN.getCode().equals(userRole) && !String.valueOf(id).equals(loginUserId)) {
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
        // 参数复制
        User user = new User();
        BeanUtil.copyProperties(dto, user);

        // 如果有密码，则需重新加密再赋值
        String userPassword = dto.getUserPassword();
        if (StrUtil.isNotBlank(userPassword)) {
            // 长度不能小于8位
            if (userPassword.length() < 8) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码长度不能小于8位");
            }
            // 重新加密再赋值
            user.setUserPassword(DigestUtil.md5Hex(userPassword));
        }

        // 如果有传递角色信息，则需判断当前用户是否有权限修改
        Integer userRole = dto.getUserRole();
        RoleEnum roleEnum = RoleEnum.getEnumByCode(userRole);
        if (roleEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请输入有效的角色信息");
        }

        // 获取登录用户角色信息
        UserVo loginUser = this.getLoginUser(request);
        Integer loginUserRole = loginUser.getUserRole();
        RoleEnum loginUserRoleEnum = RoleEnum.getEnumByCode(loginUserRole);

        // 如果不是管理员，则不能传递管理员角色信息
        if (!RoleEnum.ADMIN.equals(loginUserRoleEnum) && RoleEnum.ADMIN.equals(roleEnum)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }

        // 更新
        return this.updateById(user);
    }

    @Override
    public String uploadAvatar(MultipartFile multipartFile) {
        // 判断文件名是否存在
        String originalFilename = multipartFile.getOriginalFilename();
        if (StrUtil.isBlank(originalFilename)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 判断后缀名是否存在
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        if (StrUtil.isBlank(suffix)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 生成随机文件名
        String newFileName = UUID.randomUUID().toString().replace("-", "") + suffix;
        // 上传图片
        File file = new File(address + "/" + newFileName);

        try {
            multipartFile.transferTo(file);
        } catch (Exception e) {
            log.error("图片上传失败", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "图片上传失败");
        }

        // 返回图片请求地址
        return domain + "/user/get/avatar/" + newFileName;
    }

    @Override
    public void getAvatar(String fileName, HttpServletResponse response) {
        // 获取图片后缀名
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
        // 获取图片存放路径
        String url = address + "/" + fileName;
        // 响应图片
        response.setContentType("image/" + suffix);

        // 从服务器读取图片
        try (
                // 获取输出流
                OutputStream outputStream = response.getOutputStream();
                // 获取输入流
                FileInputStream inputStream = new FileInputStream(url)
        ) {
            // 将输入流中的数据复制到输出流中
            IoUtil.copy(inputStream, outputStream);
        } catch (IOException e) {
            log.error("文件读取失败", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "文件读取失败");
        }
    }
}




