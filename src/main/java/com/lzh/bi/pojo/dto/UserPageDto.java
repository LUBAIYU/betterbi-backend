package com.lzh.bi.pojo.dto;

import com.lzh.bi.utils.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author lzh
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserPageDto extends PageRequest {

    /**
     * 用户账号
     */
    private String userAccount;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户角色
     */
    private Integer userRole;
}
