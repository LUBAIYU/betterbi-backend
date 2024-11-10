package com.lzh.bi.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.lzh.bi.enums.ErrorCode;
import com.lzh.bi.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Excel 工具类
 *
 * @author lzh
 */
@Slf4j
public class ExcelUtil {

    /**
     * 将 Excel 文件转为 CSV 数据
     *
     * @param file 文件
     * @return String
     */
    public static String excelToCsv(MultipartFile file) {
        // 从文件中读取数据
        List<Map<Integer, String>> dataMapList;
        try {
            dataMapList = EasyExcel.read(file.getInputStream())
                    .excelType(ExcelTypeEnum.XLSX)
                    .sheet()
                    .headRowNumber(0)
                    .doReadSync();
        } catch (IOException e) {
            log.error("文件读取失败", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "文件读取失败");
        }
        if (CollUtil.isEmpty(dataMapList)) {
            return "";
        }

        // 封装返回数据
        StringBuilder res = new StringBuilder();

        // 过滤值为null的数据
        LinkedHashMap<Integer, String> headMap = (LinkedHashMap) dataMapList.get(0);
        // 过滤表头
        List<String> headList = headMap.values()
                .stream()
                .filter(ObjectUtil::isNotNull)
                .collect(Collectors.toList());
        res.append(StringUtils.join(headList, ",")).append("\n");

        // 过滤数据
        for (int i = 1; i < dataMapList.size(); i++) {
            LinkedHashMap<Integer, String> dataMap = (LinkedHashMap) dataMapList.get(i);
            List<String> dataList = dataMap.values().stream()
                    .filter(ObjectUtil::isNotNull)
                    .collect(Collectors.toList());
            res.append(StringUtils.join(dataList, ",")).append("\n");
        }

        // 返回字符串
        return res.toString();
    }
}
