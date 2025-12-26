package com.example.oms.common.entity;

import java.time.Duration;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Refill;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RateLimitType {
    
    // 1. Strict Login: 5 attempts per minute
    LOGIN(Bandwidth.classic(5, Refill.greedy(5, Duration.ofMinutes(1)))),
    
    // 2. Orders: 10 orders per minute (prevent spam)
    ORDER(Bandwidth.classic(10, Refill.greedy(10, Duration.ofMinutes(1)))),
    
    // 3. General: 50 requests per minute (optional safety net)
    GENERAL(Bandwidth.classic(50, Refill.greedy(50, Duration.ofMinutes(1))));

    private final Bandwidth limit;
}
