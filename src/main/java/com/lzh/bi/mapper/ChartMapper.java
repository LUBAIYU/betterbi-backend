package com.lzh.bi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lzh.bi.pojo.entity.Chart;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * @author lzh
 */
@Mapper
public interface ChartMapper extends BaseMapper<Chart> {

    List<Map<String, Object>> queryChartData(String querySql);
}




