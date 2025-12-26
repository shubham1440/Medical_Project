package com.gopal.medicarex.gateway_security.config;

import com.gopal.medicarex.gateway_security.resolver.PrincipalKeyResolver;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

///**
// * Rate Limiter Configuration
// *
// * Configures Redis-backed distributed rate limiting
// * Uses principal-based key resolution for user tracking
// */
//@Configuration
//public class RateLimiterConfig {
//
//    /**
//     * Principal-based Key Resolver
//     * Rate limits are tracked per user ID and department
//     */
//    @Bean
//    @Primary
//    public KeyResolver principalKeyResolver() {
//        return new PrincipalKeyResolver();
//    }
//
//    /**
//     * Default Redis Rate Limiter Bean
//     * Individual routes can override these defaults
//     */
//    @Bean
//    public RedisRateLimiter redisRateLimiter() {
//        // replenishRate: tokens per second
//        // burstCapacity: maximum tokens in bucket
//        // requestedTokens: tokens consumed per request
//        return new RedisRateLimiter(100, 200, 1);
//    }
//}

@Configuration
public class RateLimiterConfig {

    @Bean
    @Primary
    public KeyResolver principalKeyResolver() {
        return new PrincipalKeyResolver();
    }

    @Bean
    @ConditionalOnProperty(name = "spring.data.redis.host")
    public RedisRateLimiter redisRateLimiter() {
        return new RedisRateLimiter(100, 200, 1);
    }
}




