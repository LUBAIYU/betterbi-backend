package com.lzh.bi.pojo.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * @author lzh
 */
@Data
public class UserUpdateDto implements Serializable {

    /**
     * 主键ID
     */
    @NotNull(message = "用户ID不能为空")
    private Long id;

    /**
     * 账号
     */
    @NotBlank(message = "账号不能为空")
    @Size(min = 4, message = "账号长度不能小于4位")
    private String userAccount;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    @Size(min = 8, message = "密码长度不能小于8位")
    private String userPassword;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 用户角色：0-管理员/1-普通用户
     */
    @NotNull(message = "用户角色不能为空")
    private Integer userRole;
}
