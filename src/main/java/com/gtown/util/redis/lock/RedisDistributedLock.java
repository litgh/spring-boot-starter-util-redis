package com.gtown.util.redis.lock;

import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RLock;

import javax.annotation.Resource;

/**
 * @author Lee
 * @date 2016/11/7 0007
 */
@Slf4j
public class RedisDistributedLock {
    @Resource
    private Redisson redission;

    public RLock getLock(String name) {
        return redission.getLock(name);
    }
}
