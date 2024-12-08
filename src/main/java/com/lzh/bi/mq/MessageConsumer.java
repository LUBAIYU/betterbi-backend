package com.lzh.bi.mq;

import cn.hutool.core.util.StrUtil;
import com.lzh.bi.constants.MqConstant;
import com.lzh.bi.enums.ErrorCode;
import com.lzh.bi.enums.StatusEnum;
import com.lzh.bi.exception.BusinessException;
import com.lzh.bi.manager.AiManager;
import com.lzh.bi.pojo.entity.Chart;
import com.lzh.bi.service.ChartService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * 消息消费者
 *
 * @author lzh
 */
@Component
@Slf4j
public class MessageConsumer {

    @Resource
    private ChartService chartService;

    @Resource
    private AiManager aiManager;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = MqConstant.BI_QUEUE),
            exchange = @Exchange(name = MqConstant.BI_EXCHANGE),
            key = MqConstant.BI_ROUTING_KEY
    ), ackMode = "MANUAL")
    public void handleMessage(String message, Channel channel,
                              @Header(value = AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {
        Chart chart = null;
        try {
            log.info("received message = {}", message);

            // 校验消息
            if (StrUtil.isBlank(message)) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "消息为空");
            }

            // 获取图表信息和用户输入
            long chartId = Long.parseLong(message);
            chart = chartService.getById(chartId);
            String userInput = buildUserInput(chart);

            // 更新图表状态为执行中
            chart.setStatus(StatusEnum.RUNNING.getValue());
            boolean isUpdate = chartService.updateById(chart);
            if (!isUpdate) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR);
            }

            // 调用AI获取结果
            String res = aiManager.sendMsgToXingHuo(true, userInput);
            // 对结果进行切割
            String[] results = res.split("'【【【【【'");
            int len = 3;
            if (results.length < len) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "AI生成结果错误");
            }
            String genChart = results[1].trim();
            String genResult = results[2].trim();

            // 更新图表状态为成功以及更新图表数据
            chart.setStatus(StatusEnum.SUCCESS.getValue());
            chart.setGenChart(genChart);
            chart.setGenResult(genResult);
            boolean updated = chartService.updateById(chart);
            if (!updated) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR);
            }

            // 确认消息成功
            channel.basicAck(deliveryTag, false);
        } catch (BusinessException be) {
            log.error(be.getMessage());
            if (chart != null) {
                chartService.handleUpdateError(chart.getId(), be.getMessage());
            }
            // 拒绝消息
            channel.basicNack(deliveryTag, false, false);
        }
    }

    /**
     * 构造用户输入
     *
     * @param chart 图表信息
     * @return 用户输入
     */
    private String buildUserInput(Chart chart) {
        String goal = chart.getGoal();
        String chartType = chart.getChartType();
        String csvData = chart.getChartData();

        // 拼接请求，向AI输入数据
        StringBuilder userInput = new StringBuilder();
        userInput.append("分析目标：").append(goal).append("\n");
        if (StrUtil.isNotBlank(chartType)) {
            userInput.append("请精确使用").append(chartType).append("\n");
        }
        userInput.append("数据：").append(csvData).append("\n");
        return userInput.toString();
    }
}
