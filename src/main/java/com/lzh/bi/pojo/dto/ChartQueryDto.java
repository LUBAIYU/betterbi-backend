package com.lzh.bi.pojo.dto;

import com.lzh.bi.utils.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author lzh
 */

@EqualsAndHashCode(callSuper = true)
@Data
public class ChartQueryDto extends PageRequest implements Serializable {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 分析目标
     */
    private String goal;

    /**
     * 图表类型
     */
    private String chartType;
}
