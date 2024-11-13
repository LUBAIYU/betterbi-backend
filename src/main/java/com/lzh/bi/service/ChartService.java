package com.lzh.bi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lzh.bi.pojo.dto.*;
import com.lzh.bi.pojo.entity.Chart;
import com.lzh.bi.pojo.vo.AiRespVo;
import com.lzh.bi.pojo.vo.ChartVo;
import com.lzh.bi.utils.PageBean;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

/**
 * @author lzh
 */
public interface ChartService extends IService<Chart> {

    /**
     * 新增图表
     *
     * @param dto     ChartAddDto
     * @param request HttpServletRequest
     * @return 图标ID
     */
    long addChart(ChartAddDto dto, HttpServletRequest request);

    /**
     * 根据ID删除图表信息
     *
     * @param id      图表ID
     * @param request HttpServletRequest
     * @return 是否删除成功
     */
    boolean delChartById(Long id, HttpServletRequest request);

    /**
     * 更新图表信息
     *
     * @param dto     图表数据
     * @param request HttpServletRequest
     * @return 是否修改成功
     */
    boolean updateChartById(ChartUpdateDto dto, HttpServletRequest request);

    /**
     * 根据ID查询图表信息
     *
     * @param id      图表ID
     * @param request HttpServletRequest
     * @return 图表数据
     */
    Chart selectChartById(Long id, HttpServletRequest request);

    /**
     * 分页查询登录用户图表
     *
     * @param dto     查询参数
     * @param request HttpServletRequest
     * @return ChartVo列表
     */
    PageBean<ChartVo> listMyChartByPage(ChartQueryDto dto, HttpServletRequest request);

    /**
     * 管理员分页查询图表
     *
     * @param dto 查询参数
     * @return Chart列表
     */
    PageBean<Chart> listChartsByPage(ChartQueryAdminDto dto);

    /**
     * AI生成图表
     *
     * @param multipartFile 数据文件
     * @param dto           生成目标
     * @param request       HttpServletRequest
     * @return AI生成结果
     */
    AiRespVo genChartByAi(MultipartFile multipartFile, ChartGenDto dto, HttpServletRequest request);
}
