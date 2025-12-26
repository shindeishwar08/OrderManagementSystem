package com.example.oms.common.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.example.oms.common.entity.RateLimitType;

import io.github.bucket4j.Bucket;

@Service
public class RateLimiterService {
    
    private final Map<String,Bucket> rateCache = new ConcurrentHashMap<>();

    // private Bucket createNewBucket(){
    //     Bandwidth limit = Bandwidth.classic(10, Refill.greedy(10, Duration.ofMinutes(1)));
    //     return Bucket.builder().addLimit(limit).build();
    // }

    public Bucket resolveBucket(RateLimitType type,String key){

        String cacheKey = type.toString() + ":" + key;

        return rateCache.computeIfAbsent(cacheKey, k-> Bucket.builder().addLimit(type.getLimit()).build());
    }
}
