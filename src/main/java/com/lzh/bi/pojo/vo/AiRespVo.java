package com.lzh.bi.pojo.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author lzh
 */
@Data
public class AiRespVo implements Serializable {

    /**
     * 图表ID
     */
    private Long chartId;

    /**
     * 生成的图表数据
     */
    private String genChart;

    /**
     * 生成的分析结论
     */
    private String genResult;
}
