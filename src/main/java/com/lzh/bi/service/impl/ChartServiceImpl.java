package com.lzh.bi.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lzh.bi.constants.CommonConst;
import com.lzh.bi.enums.ErrorCode;
import com.lzh.bi.enums.RoleEnum;
import com.lzh.bi.enums.StatusEnum;
import com.lzh.bi.exception.BusinessException;
import com.lzh.bi.manager.AiManager;
import com.lzh.bi.manager.RedisLimiterManager;
import com.lzh.bi.mapper.ChartMapper;
import com.lzh.bi.mq.MessageProducer;
import com.lzh.bi.pojo.dto.*;
import com.lzh.bi.pojo.entity.Chart;
import com.lzh.bi.pojo.vo.AiRespVo;
import com.lzh.bi.pojo.vo.ChartVo;
import com.lzh.bi.pojo.vo.UserVo;
import com.lzh.bi.service.ChartService;
import com.lzh.bi.service.UserService;
import com.lzh.bi.utils.ExcelUtil;
import com.lzh.bi.utils.PageBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author lzh
 */
@Service
@Slf4j
public class ChartServiceImpl extends ServiceImpl<ChartMapper, Chart>
        implements ChartService {

    @Resource
    private UserService userService;

    @Resource
    private AiManager aiManager;

    @Resource
    private RedisLimiterManager redisLimiterManager;

    @Resource
    private MessageProducer messageProducer;

    @Override
    public long addChart(ChartAddDto dto, HttpServletRequest request) {
        // 获取登录用户ID
        UserVo userVo = userService.getLoginUser(request);
        String userId = userVo.getId();

        // 插入图表数据
        Chart chart = new Chart();
        BeanUtil.copyProperties(dto, chart);
        chart.setUserId(Long.parseLong(userId));
        this.save(chart);

        return chart.getId();
    }

    @Override
    public boolean delChartById(Long id, HttpServletRequest request) {
        // 获取登录用户信息
        UserVo userVo = userService.getLoginUser(request);
        String userId = userVo.getId();
        Integer userRole = userVo.getUserRole();

        // 根据ID获取图表数据
        Chart chart = this.getById(id);
        if (chart == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }

        // 获取图表的创建用户ID
        Long createUserId = chart.getUserId();

        // 如果登录用户不是管理员，且创建用户ID和登录用户ID不一致，则抛出权限异常
        if (!RoleEnum.ADMIN.getCode().equals(userRole) && !userId.equals(String.valueOf(createUserId))) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }

        // 删除
        return this.removeById(id);
    }

    @Override
    public boolean updateChartById(ChartUpdateDto dto, HttpServletRequest request) {
        // 获取登录用户信息
        UserVo userVo = userService.getLoginUser(request);
        Long userId = Long.parseLong(userVo.getId());
        Integer userRole = userVo.getUserRole();

        // 根据ID获取图表数据
        Chart chart = this.getById(dto.getId());
        if (chart == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }

        // 获取图表的创建用户ID
        Long createUserId = chart.getUserId();

        // 如果登录用户不是管理员，且创建用户ID和登录用户ID不一致，则抛出权限异常
        if (!RoleEnum.ADMIN.getCode().equals(userRole) && !userId.equals(createUserId)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }

        // 修改
        BeanUtil.copyProperties(dto, chart);
        return this.updateById(chart);
    }

    @Override
    public Chart selectChartById(Long id, HttpServletRequest request) {
        // 获取登录用户信息
        UserVo userVo = userService.getLoginUser(request);
        Long loginUserId = Long.parseLong(userVo.getId());
        Integer userRole = userVo.getUserRole();

        // 如果是管理员，则直接根据ID查询
        if (RoleEnum.ADMIN.getCode().equals(userRole)) {
            return this.getById(id);
        }

        // 如果是用户，则需查出当前用户创建的图表
        return this.lambdaQuery()
                .eq(Chart::getId, id)
                .eq(Chart::getUserId, loginUserId)
                .one();
    }

    @Override
    public PageBean<ChartVo> listMyChartByPage(ChartQueryDto dto, HttpServletRequest request) {
        // 获取登录用户ID
        UserVo userVo = userService.getLoginUser(request);
        Long loginUserId = Long.parseLong(userVo.getId());

        // 添加分页参数
        IPage<Chart> pageParam = new Page<>(dto.getCurrent(), dto.getPageSize());

        // 查询
        IPage<Chart> result = this.lambdaQuery()
                .eq(dto.getId() != null, Chart::getId, dto.getId())
                .like(StrUtil.isNotBlank(dto.getName()), Chart::getName, dto.getName())
                .like(StrUtil.isNotBlank(dto.getGoal()), Chart::getGoal, dto.getGoal())
                .like(StrUtil.isNotBlank(dto.getChartType()), Chart::getChartType, dto.getChartType())
                .eq(Chart::getUserId, loginUserId)
                .orderByDesc(Chart::getCreateTime)
                .page(pageParam);

        // 如果为空直接返回
        List<Chart> records = result.getRecords();
        if (CollectionUtil.isEmpty(records)) {
            return PageBean.of(result.getTotal(), new ArrayList<>());
        }

        // 转为VO返回
        List<ChartVo> chartVoList = records.stream().map(item -> {
            ChartVo chartVo = new ChartVo();
            BeanUtil.copyProperties(item, chartVo);
            return chartVo;
        }).collect(Collectors.toList());
        return PageBean.of(result.getTotal(), chartVoList);
    }

    @Override
    public PageBean<Chart> listChartsByPage(ChartQueryAdminDto dto) {
        // 添加分页参数
        IPage<Chart> pageParam = new Page<>(dto.getCurrent(), dto.getPageSize());

        // 查询
        IPage<Chart> result = this.lambdaQuery()
                .eq(dto.getId() != null, Chart::getId, dto.getId())
                .like(StrUtil.isNotBlank(dto.getName()), Chart::getName, dto.getName())
                .like(StrUtil.isNotBlank(dto.getGoal()), Chart::getGoal, dto.getGoal())
                .like(StrUtil.isNotBlank(dto.getChartType()), Chart::getChartType, dto.getChartType())
                .eq(dto.getUserId() != null, Chart::getUserId, dto.getUserId())
                .orderByDesc(Chart::getCreateTime)
                .page(pageParam);

        return PageBean.of(result.getTotal(), result.getRecords());
    }

    @Override
    public AiRespVo genChartByAi(MultipartFile multipartFile, ChartGenDto dto, HttpServletRequest request) {
        // 获取登录用户信息
        UserVo userVo = userService.getLoginUser(request);
        Long userId = Long.parseLong(userVo.getId());

        // 执行限流操作
        redisLimiterManager.doRateLimit("genChartByAi_" + userId);

        String name = dto.getName();
        String goal = dto.getGoal();
        String chartType = dto.getChartType();

        // 校验文件
        checkFile(multipartFile);

        // 将Excel数据转换为CSV格式
        String csvData = ExcelUtil.excelToCsv(multipartFile);

        // 保存图表数据
        Chart chart = new Chart();
        chart.setUserId(userId);
        chart.setName(name);
        chart.setGoal(goal);
        chart.setChartType(chartType);
        chart.setChartData(csvData);
        chart.setStatus(StatusEnum.WAITING.getValue());
        boolean save = this.save(chart);
        if (!save) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "图表数据保存失败");
        }

        // 发送消息到MQ
        long chartId = chart.getId();
        messageProducer.sendMessage(String.valueOf(chartId));

        // 返回结果
        AiRespVo respVo = new AiRespVo();
        respVo.setChartId(chartId);
        return respVo;
    }

    /**
     * 更新图表状态为失败
     *
     * @param chartId     图表ID
     * @param execMessage 错误信息
     */
    @Override
    public void handleUpdateError(Long chartId, String execMessage) {
        Chart chart = new Chart();
        chart.setId(chartId);
        chart.setExecMessage(execMessage);
        chart.setStatus(StatusEnum.FAILED.getValue());
        this.updateById(chart);
    }

    /**
     * 校验文件是否符合要求
     *
     * @param file 文件
     */
    private void checkFile(MultipartFile file) {
        // 获取文件大小
        long size = file.getSize();
        // 获取文件原始文件名
        String filename = file.getOriginalFilename();
        if (size > CommonConst.FILE_MAX_SIZE) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件大小超过1MB");
        }
        // 获取文件后缀
        String suffix = FileUtil.getSuffix(filename);
        if (!CommonConst.FILE_TYPE_LIST.contains(suffix)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件类型不支持");
        }
    }
}




