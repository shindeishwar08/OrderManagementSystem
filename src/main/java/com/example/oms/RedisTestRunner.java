package com.example.oms;

import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RedisTestRunner implements CommandLineRunner {

    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public void run(String... args) throws Exception {
        // 1. Write to Redis
        redisTemplate.opsForValue().set("my-test-key", "It works!");

        // 2. Read from Redis
        String value = redisTemplate.opsForValue().get("my-test-key");

        // 3. Print
        System.out.println("🔴 REDIS CHECK: " + value);
    }
}