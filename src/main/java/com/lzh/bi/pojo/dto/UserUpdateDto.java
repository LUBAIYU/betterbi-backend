package com.lzh.bi.pojo.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
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
    private String userAccount;

    /**
     * 密码
     */
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
    private Integer userRole;
}
