package com.example.library_management.global.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class RedissonConfig {

    // 로컬 환경 설정
    @Configuration
    @Profile("dev")
    public static class LocalRedissonConfig{
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
                    .setDnsMonitoringInterval(5000)
                    .setSubscriptionConnectionMinimumIdleSize(1)
                    .setSubscriptionConnectionPoolSize(2)
                    .setTimeout(3000);

            return Redisson.create(config);

        }
    }
    // 운영 환경 설정
    @Configuration
    @Profile("prod")
    public static class ProdRedissonConfig {
        @Value("${spring.data.redis.sentinel.master}")
        private String master;

        @Value("${spring.data.redis.sentinel.nodes}")
        private String sentinelNodes;

        @Value("${spring.data.redis.password}")
        private String password;

        @Bean
        public RedissonClient redissonClient() {
            Config config = new Config();
            config.useSentinelServers()
                    .setMasterName(master)
                    .setPassword(password)
                    .setConnectTimeout(3000)
                    .setMasterConnectionMinimumIdleSize(1)
                    .setMasterConnectionPoolSize(2)
                    .setSlaveConnectionMinimumIdleSize(1)
                    .setSlaveConnectionPoolSize(2)
                    .setRetryAttempts(3)
                    .setRetryInterval(1500)
                    .setTimeout(3000);

            String[] nodes = sentinelNodes.split(",");
            for (String node : nodes) {
                config.useSentinelServers().addSentinelAddress("redis://" + node.trim());
            }

            return Redisson.create(config);
        }
    }
}
