package com.example.library_management.global.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {

    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private int redisPort;

    @Value("${spring.data.redis.password}")
    private String password;

    @Bean
    public RedissonClient redissonClient() {

        //redisson 설정을 정의하는데 사용
        Config config = new Config();

        // Redis 연결 설정
        config.useSingleServer()
                .setAddress("redis://" + redisHost + ":" + redisPort)
                .setPassword(password)
                .setConnectionMinimumIdleSize(1)
                .setConnectionPoolSize(2)
                .setRetryAttempts(3)
                .setRetryInterval(1500)
                .setTimeout(3000);

        return Redisson.create(config);

    }
}
