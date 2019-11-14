package com.gtown.util.redis;

import com.baidu.fsg.uid.impl.CachedUidGenerator;
import com.baidu.fsg.uid.impl.DefaultUidGenerator;
import com.gtown.util.redis.config.UidGeneratorProperties;
import com.gtown.util.redis.sequence.SequenceGenerator;
import com.gtown.util.redis.sequence.TimebaseSequenceGenerator;
import org.redisson.Redisson;
import org.redisson.spring.starter.RedissonAutoConfiguration;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@AutoConfigureAfter(RedissonAutoConfiguration.class)
@Configuration
public class IdGeneratorAutoConfiguration {
    @Bean
    @ConfigurationProperties("util.redis.uid")
    @ConditionalOnMissingBean(UidGeneratorProperties.class)
    public UidGeneratorProperties uidGeneratorProperties() {
        return new UidGeneratorProperties();
    }

    @Bean
    public CachedUidGenerator cachedUidGenerator(Redisson redis, UidGeneratorProperties properties) {
        CachedUidGenerator cachedUidGenerator = new CachedUidGenerator(() -> redis.getAtomicLong(properties.getWorkIdKey()).incrementAndGet());
        BeanUtils.copyProperties(cachedUidGenerator, properties);
        return cachedUidGenerator;
    }

    @Bean
    public DefaultUidGenerator defaultUidGenerator(Redisson redis, UidGeneratorProperties properties) {
        return new DefaultUidGenerator(() -> redis.getAtomicLong(properties.getWorkIdKey()).incrementAndGet());
    }

    @Bean
    public SequenceGenerator sequenceGenerator() {
        return new SequenceGenerator();
    }

    @Bean
    public TimebaseSequenceGenerator timebaseSequenceGenerator() {
        return new TimebaseSequenceGenerator();
    }
}
