package com.bavya.authservice.redis;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/redis")
public class RedisTestController {

    private final StringRedisTemplate redisTemplate;

    public RedisTestController(
            StringRedisTemplate redisTemplate
    ) {
        this.redisTemplate = redisTemplate;
    }

    @GetMapping("/test")
    public String test() {
        System.out.println("REDIS ENDPOINT HIT");

        redisTemplate.opsForValue()
                .set("hello", "world");

        return redisTemplate
                .opsForValue()
                .get("hello");
    }
}