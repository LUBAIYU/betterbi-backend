package com.lzh.bi.manager;

import com.lzh.bi.enums.ErrorCode;
import com.lzh.bi.exception.BusinessException;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 专门提供 RedisLimiter 限流操作的基础服务（提供了通用能力）
 *
 * @author lzh
 */
@Service
public class RedisLimiterManager {

    @Resource
    private RedissonClient redissonClient;

    /**
     * 存储不同的限流器
     */
    private final Map<String, RRateLimiter> rateLimiterMap = new ConcurrentHashMap<>();

    /**
     * 初始化限流器
     *
     * @param key 区分不同的限流器
     */
    public void initRateLimiter(String key) {
        RRateLimiter rateLimiter = redissonClient.getRateLimiter(key);
        // 1s 内最多访问 2 次
        rateLimiter.trySetRate(RateType.OVERALL, 2, 1, RateIntervalUnit.SECONDS);
        rateLimiterMap.put(key, rateLimiter);
    }


    /**
     * 限流操作
     *
     * @param key 区分不同的限流器
     */
    public void doRateLimit(String key) {
        // 获取限流器，如果不存在则进行初始化
        RRateLimiter rateLimiter = rateLimiterMap.get(key);
        if (rateLimiter == null) {
            initRateLimiter(key);
            rateLimiter = rateLimiterMap.get(key);
        }
        // 限流
        boolean canOp = rateLimiter.tryAcquire(1);
        if (!canOp) {
            throw new BusinessException(ErrorCode.TOO_MANY_REQUEST);
        }
    }
}
