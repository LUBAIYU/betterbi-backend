package com.lzh.bi.manager;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
public class XingHuoTest {

    @Resource
    private AiManager aiManager;

    @Test
    public void test() {
        String message = "分析需求：\n" +
                "分析网站用户的增长情况\n" +
                "原始数据：\n" +
                "日期,用户数\n" +
                "1号,10\n" +
                "2号,20\n" +
                "3号,30";
        System.out.println("res=" + aiManager.sendMsgToXingHuo(true, message));
    }
}
