package com.lzh.bi.pojo.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * @author lzh
 */
@Data
public class UserRegisterDto implements Serializable {

    @NotBlank(message = "账号不能为空")
    @Size(min = 4, message = "账号长度不能小于4位")
    private String userAccount;

    @NotBlank(message = "密码不能为空")
    @Size(min = 8, message = "密码长度不能小于8位")
    private String userPassword;

    @NotBlank(message = "确认密码不能为空")
    @Size(min = 8, message = "确认密码长度不能小于8位")
    private String checkPassword;
}
