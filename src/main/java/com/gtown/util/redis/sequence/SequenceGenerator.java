package com.gtown.util.redis.sequence;

import org.redisson.Redisson;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RScript;
import org.redisson.client.codec.StringCodec;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 基于redis自增序列生成器
 */
public class SequenceGenerator {
    @Resource
    private Redisson                 redisson;
    private Map<String, RAtomicLong> cache = new ConcurrentHashMap<>();

    public void init(String key, Long initValue) {
        Assert.isTrue(StringUtils.hasText(key), "key cannot be null");
        Assert.isTrue(initValue > 0, "init value cannot be negative");
        redisson.getScript(StringCodec.INSTANCE).eval(RScript.Mode.READ_WRITE,
                "local v = redis.call('INCR', KEYS[1])\n; " +
                        "if tonumber(v) < tonumber(ARGV[1]) then " +
                        "redis.call('SET', KEYS[1], tonumber(ARGV[1])); \n" +
                        "return 1; " +
                        "else return 0 end", RScript.ReturnType.BOOLEAN, Arrays.asList(key), initValue.toString());
    }

    /**
     * 根据key获取自增序列
     *
     * @param key
     * @return
     */
    public long next(String key) {
        return seq(key).getAndIncrement();
    }

    /**
     * 根据key获取n个自增序列
     *
     * @param key
     * @param n
     * @return
     */
    public List<Long> next(String key, int n) {
        long       m    = seq(key).getAndAdd(n);
        List<Long> list = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            list.add(m + i);
        }
        return list;
    }

    /**
     * 根据key获取自增序列, 并格式化
     *
     * @param key
     * @param format
     * @return
     * @see String#format
     */
    public String next(String key, String format) {
        long n = next(key);
        return String.format(format, n);
    }

    /**
     * 根据key获取n个自增序列, 并格式化
     *
     * @param key
     * @param format
     * @param n
     * @return
     */
    public List<String> next(String key, String format, int n) {
        return next(key, n).stream().map(i -> String.format(format, i)).collect(Collectors.toList());
    }

    private RAtomicLong seq(String key) {
        RAtomicLong seq = cache.get(key);
        if (seq == null) {
            seq = redisson.getAtomicLong(key);
            cache.put(key, seq);
        }
        return seq;
    }
}
