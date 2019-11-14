package com.gtown.util.redis.config;


import lombok.Data;

@Data
public class UidGeneratorProperties {
    private String workIdKey  = "redis:uid:workId";
    private int    timeBits   = 28;
    private int    workerBits = 22;
    private int    seqBits    = 13;
}
