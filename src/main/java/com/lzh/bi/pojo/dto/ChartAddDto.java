package com.lzh.bi.pojo.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author lzh
 */
@Data
public class ChartAddDto implements Serializable {

    /**
     * 图表名称
     */
    @NotBlank(message = "图表名称不能为空")
    private String name;

    /**
     * 分析目标
     */
    @NotBlank(message = "分析目标不能为空")
    private String goal;

    /**
     * 图表数据
     */
    @NotBlank(message = "图表数据不能为空")
    private String chartData;

    /**
     * 图表类型
     */
    @NotBlank(message = "图表类型不能为空")
    private String chartType;
}
