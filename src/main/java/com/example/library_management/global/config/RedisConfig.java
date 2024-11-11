package com.example.library_management.global.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    // 로컬 환경 설정
    @Configuration
    @Profile("dev")
    public static class LocalRedisConfig{
        @Value("${spring.data.redis.host}")
        private String redisHost;

        @Value("${spring.data.redis.port}")
        private int redisPort;

        @Value("${spring.data.redis.password}")
        private String password;

        @Bean
        public RedisConnectionFactory redisConnectionFactory() {
            RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration();
            redisConfig.setHostName(redisHost);
            redisConfig.setPort(redisPort);
            redisConfig.setPassword(password);

            return new LettuceConnectionFactory(redisConfig);
        }

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
    public static class ProdRedisConfig {
        @Value("${spring.data.redis.sentinel.master}")
        private String master;

        @Value("${spring.data.redis.sentinel.nodes}")
        private String sentinelNodes;

        @Value("${spring.data.redis.password}")
        private String password;

        @Bean
        public RedisConnectionFactory redisConnectionFactory() {
            RedisSentinelConfiguration sentinelConfig = new RedisSentinelConfiguration();
            sentinelConfig.setMaster(master);

            String[] nodes = sentinelNodes.split(",");
            for (String node : nodes) {
                String[] parts = node.trim().split(":");
                sentinelConfig.sentinel(parts[0], Integer.parseInt(parts[1]));
            }

            sentinelConfig.setPassword(password);

            return new LettuceConnectionFactory(sentinelConfig);
        }
        @Bean
        public RedissonClient redissonClient() {
            Config config = new Config();
            String[] nodes = sentinelNodes.split(",");
            String[] sentinelAddresses = new String[nodes.length];

            // Sentinel 주소 형식 수정
            for (int i = 0; i < nodes.length; i++) {
                String node = nodes[i].trim();
                sentinelAddresses[i] = "redis://" + node;
                System.out.println("Adding sentinel address: " + sentinelAddresses[i]);
            }

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
                    .setRetryAttempts(3)
                    .setRetryInterval(1500)
                    .setTimeout(3000)
                    .setConnectTimeout(3000)
                    .setCheckSentinelsList(false)  // Sentinel 노드 체크 비활성화
                    .setDnsMonitoringInterval(5000)
                    .addSentinelAddress(sentinelAddresses);

            return Redisson.create(config);
        }
    }

    // 공통 RedisTemplate 설정
    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new StringRedisSerializer());
        redisTemplate.setEnableTransactionSupport(true);
        redisTemplate.afterPropertiesSet();

        return redisTemplate;
    }
}
