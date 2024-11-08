package com.lzh.bi.controller;

import com.lzh.bi.annotation.LoginCheck;
import com.lzh.bi.annotation.MustAdmin;
import com.lzh.bi.enums.ErrorCode;
import com.lzh.bi.exception.BusinessException;
import com.lzh.bi.pojo.dto.ChartAddDto;
import com.lzh.bi.pojo.dto.ChartQueryAdminDto;
import com.lzh.bi.pojo.dto.ChartQueryDto;
import com.lzh.bi.pojo.dto.ChartUpdateDto;
import com.lzh.bi.pojo.entity.Chart;
import com.lzh.bi.pojo.vo.ChartVo;
import com.lzh.bi.service.ChartService;
import com.lzh.bi.utils.PageBean;
import com.lzh.bi.utils.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * @author lzh
 */
@RestController
@RequestMapping("/chart")
@RequiredArgsConstructor
@Validated
public class ChartController {

    private final ChartService chartService;

    @LoginCheck
    @PostMapping("/add")
    public Result<Long> addChart(@Valid @RequestBody ChartAddDto dto, HttpServletRequest request) {
        return Result.success(chartService.addChart(dto, request));
    }

    @LoginCheck
    @DeleteMapping("/delete/{id}")
    public Result<Boolean> delChart(@PathVariable @NotNull Long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return Result.success(chartService.delChartById(id, request));
    }

    @LoginCheck
    @PutMapping("/update")
    public Result<Boolean> updateChart(@Valid @RequestBody ChartUpdateDto dto, HttpServletRequest request) {
        return Result.success(chartService.updateChartById(dto, request));
    }

    @LoginCheck
    @GetMapping("/get/{id}")
    public Result<Chart> getChartById(@NotNull @PathVariable Long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return Result.success(chartService.selectChartById(id, request));
    }

    @LoginCheck
    @PostMapping("/my/page")
    public Result<PageBean<ChartVo>> listMyChartByPage(@Valid @RequestBody ChartQueryDto dto, HttpServletRequest request) {
        return Result.success(chartService.listMyChartByPage(dto, request));
    }

    @MustAdmin
    @PostMapping("/page")
    public Result<PageBean<Chart>> listChartsByPage(@Valid @RequestBody ChartQueryAdminDto dto) {
        return Result.success(chartService.listChartsByPage(dto));
    }
}
