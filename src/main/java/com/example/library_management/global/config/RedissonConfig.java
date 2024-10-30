package com.example.library_management.global.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {

    @Value("${spring.data.redis.url}")
    private String url;

    @Bean
    public RedissonClient redissonClient() {

        //redisson 설정을 정의하는데 사용
        Config config = new Config();

        config.useSingleServer().setAddress(url);

        return Redisson.create(config);

    }
}
