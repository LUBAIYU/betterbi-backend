package com.lzh.bi.pojo.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author lzh
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ChartQueryAdminDto extends ChartQueryDto {
    /**
     * 创建用户 ID
     */
    private Long userId;
}
