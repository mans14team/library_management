//package com.example.library_management.global.config;
//
//import org.redisson.Redisson;
//import org.redisson.api.RedissonClient;
//import org.redisson.config.Config;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Profile;
//
//@Configuration
//public class RedissonConfig {
//
//    // 로컬 환경 설정
//    @Configuration
//    @Profile("dev")
//    public static class LocalRedissonConfig{
//        @Value("${spring.data.redis.host}")
//        private String redisHost;
//
//        @Value("${spring.data.redis.port}")
//        private int redisPort;
//
//        @Value("${spring.data.redis.password}")
//        private String password;
//
//        @Bean
//        public RedissonClient redissonClient() {
//
//            //redisson 설정을 정의하는데 사용
//            Config config = new Config();
//
//            // Redis 연결 설정
//            config.useSingleServer()
//                    .setAddress("redis://" + redisHost + ":" + redisPort)
//                    .setPassword(password)
//                    .setConnectionMinimumIdleSize(1)
//                    .setConnectionPoolSize(2)
//                    .setRetryAttempts(3)
//                    .setRetryInterval(1500)
//                    .setDnsMonitoringInterval(5000)
//                    .setSubscriptionConnectionMinimumIdleSize(1)
//                    .setSubscriptionConnectionPoolSize(2)
//                    .setTimeout(3000);
//
//            return Redisson.create(config);
//
//        }
//    }
//    // 운영 환경 설정
//    @Configuration
//    @Profile("prod")
//    public static class ProdRedissonConfig {
//        @Value("${spring.data.redis.sentinel.master}")
//        private String master;
//
//        @Value("${spring.data.redis.sentinel.nodes}")
//        private String sentinelNodes;
//
//        @Value("${spring.data.redis.password}")
//        private String password;
//
//        @Bean(destroyMethod = "shutdown")
//        public RedissonClient redissonClient() {
//            Config config = new Config();
//
//            String[] nodes = sentinelNodes.split(",");
//            String[] sentinelAddresses = new String[nodes.length];
//
//            // Sentinel 주소 형식 수정
//            for (int i = 0; i < nodes.length; i++) {
//                String node = nodes[i].trim();
//                // 포트 번호가 중복되는 문제 해결
//                if (node.split(":").length > 2) {
//                    // 마지막 포트 번호만 사용
//                    String[] parts = node.split(":");
//                    node = parts[0] + ":" + parts[1];
//                }
//                sentinelAddresses[i] = "redis://" + node;
//                System.out.println("Adding sentinel address: " + sentinelAddresses[i]);
//            }
//
//            // Redis 설정
//            config.useSentinelServers()
//                    .setMasterName(master)
//                    .setPassword(password)
//                    .setSentinelPassword(password)
//                    .setDatabase(0)
//                    .setMasterConnectionMinimumIdleSize(1)
//                    .setMasterConnectionPoolSize(2)
//                    .setSlaveConnectionMinimumIdleSize(1)
//                    .setSlaveConnectionPoolSize(2)
//                    .setSubscriptionConnectionMinimumIdleSize(1)
//                    .setSubscriptionConnectionPoolSize(2)
//                    .setRetryAttempts(3)
//                    .setRetryInterval(1500)
//                    .setDnsMonitoringInterval(5000)
//                    .setFailedSlaveReconnectionInterval(3000)
//                    .setFailedSlaveCheckInterval(60000)
//                    .setTimeout(3000)
//                    .setConnectTimeout(3000)
//                    .addSentinelAddress(sentinelAddresses);
//
//            return Redisson.create(config);
//        }
//    }
//}
