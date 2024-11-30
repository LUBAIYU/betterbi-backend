package com.lzh.bi.enums;

import lombok.Getter;

/**
 * 图表状态枚举
 *
 * @author lzh
 */
@Getter
public enum StatusEnum {

    /**
     * 状态枚举：等待中、成功、失败、执行中
     */
    WAITING(0, "等待中"),
    SUCCESS(1, "成功"),
    FAILED(2, "失败"),
    RUNNING(3, "执行中");

    private final Integer value;
    private final String text;

    StatusEnum(Integer value, String text) {
        this.value = value;
        this.text = text;
    }
}
