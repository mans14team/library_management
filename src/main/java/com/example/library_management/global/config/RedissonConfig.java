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

        @Bean(destroyMethod = "shutdown")
        public RedissonClient redissonClient() {
            Config config = new Config();

            String[] nodes = sentinelNodes.split(",");
            String[] sentinelAddresses = new String[nodes.length];

            // Sentinel 주소 구성
            for (int i = 0; i < nodes.length; i++) {
                String[] parts = nodes[i].trim().split(":");
                String host = parts[0];
                String port = parts.length > 1 ? parts[1] : "26379";
                sentinelAddresses[i] = "redis://" + host + ":" + port;

                // 디버깅용 로그
                System.out.println("Configuring sentinel node: " + sentinelAddresses[i]);
            }

            // Redis 설정
            config.useSentinelServers()
                    .setMasterName(master)
                    .setPassword(password)
                    .setSentinelPassword(password)
                    .setDatabase(0)
                    .setMasterConnectionMinimumIdleSize(1)
                    .setMasterConnectionPoolSize(2)
                    .setSlaveConnectionMinimumIdleSize(1)
                    .setSlaveConnectionPoolSize(2)
                    .setSubscriptionConnectionMinimumIdleSize(1)
                    .setSubscriptionConnectionPoolSize(2)
                    .setRetryAttempts(5)
                    .setRetryInterval(3000)
                    .setDnsMonitoringInterval(30000)
                    .setFailedSlaveReconnectionInterval(3000)
                    .setFailedSlaveCheckInterval(60000)
                    .setTimeout(10000)
                    .setConnectTimeout(10000)
                    .setCheckSentinelsList(false)
                    .addSentinelAddress(sentinelAddresses);

            return Redisson.create(config);
        }
    }
}
