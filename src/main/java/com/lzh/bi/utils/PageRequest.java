package com.lzh.bi.utils;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 通用分页参数
 *
 * @author by
 */
@Data
public class PageRequest implements Serializable {
    /**
     * 当前页码
     */
    @NotNull
    private Integer current = 1;
    /**
     * 每页记录数
     */
    @NotNull
    private Integer pageSize = 10;
}
