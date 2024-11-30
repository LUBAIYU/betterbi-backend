package com.lzh.bi.enums;

import lombok.Getter;

/**
 * 用户角色枚举
 *
 * @author by
 */
@Getter
public enum RoleEnum {

    /**
     * 管理员
     */
    ADMIN(0),
    /**
     * 普通用户
     */
    USER(1);

    private final Integer code;

    RoleEnum(Integer code) {
        this.code = code;
    }

    public static RoleEnum getEnumByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (RoleEnum roleEnum : RoleEnum.values()) {
            if (roleEnum.getCode().equals(code)) {
                return roleEnum;
            }
        }
        return null;
    }
}
