package com.example.oms.user;

import java.util.concurrent.TimeUnit;

import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RedisPlayground implements CommandLineRunner {
    
    private final RedisTemplate<String,String> redisTemplate;

    public void run(String... args) throws Exception{
        
        // String sessionKey = "session:123";

        // redisTemplate.opsForValue().set(sessionKey, "active",10, TimeUnit.SECONDS);

        // String userKey = "user:100";

        // redisTemplate.opsForHash().put(userKey, "name", "John Doe");
        // redisTemplate.opsForHash().put(userKey,"role","Partner");
        // redisTemplate.opsForHash().put(userKey,"status","available");

        // System.out.println("Saved Hash"+userKey);

        // String name = (String) redisTemplate.opsForHash().get(userKey,"name");
        // String role = (String) redisTemplate.opsForHash().get(userKey,"role");

        // System.out.println("Read from Hash -> "+name+" and "+role);

        String sessionKey = "session:123";

        redisTemplate.opsForValue().set(sessionKey, "active", 10, TimeUnit.SECONDS);

        String userKey = "user:100";

        redisTemplate.opsForHash().put(userKey, "name", "John Doe");
        redisTemplate.opsForHash().put(userKey, "Role", "Partner");
        redisTemplate.opsForHash().put(userKey, "status", "available");

        String name = (String)redisTemplate.opsForHash().get(userKey, "name");
        String role = (String)redisTemplate.opsForHash().get(userKey, "status");

        System.out.println("Read from Hash -> "+name+" and "+role);


    }
    
}
