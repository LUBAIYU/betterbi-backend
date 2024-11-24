package com.lzh.bi.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@SpringBootTest
class ChartMapperTest {

    @Resource
    private ChartMapper chartMapper;

    @Test
    void queryChartData() {
        String querySql = "select * from chart_1858151445057720322";
        List<Map<String, Object>> mapList = chartMapper.queryChartData(querySql);
        System.out.println(mapList);
    }
}