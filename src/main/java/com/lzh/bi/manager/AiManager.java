package com.lzh.bi.manager;

import io.github.briqt.spark4j.SparkClient;
import io.github.briqt.spark4j.constant.SparkApiVersion;
import io.github.briqt.spark4j.model.SparkMessage;
import io.github.briqt.spark4j.model.SparkSyncChatResponse;
import io.github.briqt.spark4j.model.request.SparkRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 讯飞星火大模型接口调用封装类
 *
 * @author lzh
 */
@Service
@Slf4j
public class AiManager {

    @Resource
    private SparkClient sparkClient;

    /**
     * 发送消息到讯飞星火大模型
     *
     * @param isUseTemplate 角色设定模版 true/false
     * @param message       用户传递的分析需求
     *                      分析需求：
     *                      分析网站用户的增长情况
     *                      原始数据：
     *                      日期,用户数
     *                      1号,10
     *                      2号,20
     *                      3号,30
     * @return 生成结果
     */
    public String sendMsgToXingHuo(boolean isUseTemplate, String message) {
        // 是否使用系统预设
        if (isUseTemplate) {
            String template = "你是一个数据分析师和前端开发专家，接下来我会按照以下固定格式给你提供内容：\n" +
                    "分析需求：\n" +
                    "{数据分析的需求或者目标}\n" +
                    "原始数据：\n" +
                    "{csv格式的原始数据，用,作为分隔符}\n" +
                    "请根据这两部分内容，严格按照以下指定格式生成内容（此外不要输出任何多余的开头、结尾、注释）同时不要使用这个符号 '】'\n" +
                    "'【【【【【'\n" +
                    "{前端 Echarts V5 的 option 配置对象 JSON 代码, 不要生成任何多余的内容，比如注释和代码块标记}\n" +
                    "'【【【【【'\n" +
                    "{明确的数据分析结论、越详细越好，不要生成多余的注释} \n"
                    + "下面是一个具体的例子的模板："
                    + "'【【【【【'\n"
                    + "{JSON格式代码}"
                    + "'【【【【【'\n" +
                    "{结论}";
            message = template + "\n" + message;
        }

        // 构造请求
        List<SparkMessage> messageList = new ArrayList<>();
        messageList.add(SparkMessage.userContent(message));
        SparkRequest sparkRequest = SparkRequest.builder()
                // 发给大模型的指令
                .messages(messageList)
                // 大模型版本
                .apiVersion(SparkApiVersion.V4_0)
                // 模型回答的tokens的最大长度
                .maxTokens(2048)
                // 采样阈值，值越大，越容易生成不一样的内容
                .temperature(0.2)
                .build();

        // 调用大模型
        SparkSyncChatResponse chatResponse = sparkClient.chatSync(sparkRequest);
        // 打印日志
        log.info("星火大模型返回结果：{}", chatResponse.getContent());
        return chatResponse.getContent();
    }
}
