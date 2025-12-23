package com.healthcare.config.security;

import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class RateLimitConfig {

    @Value("${medicarex.rate-limit.patient}")
    private int patientLimit;

    @Value("${medicarex.rate-limit.provider}")
    private int providerLimit;

    @Value("${medicarex.rate-limit.admin}")
    private int adminLimit;

    @Bean
    public RateLimiterRegistry rateLimiterRegistry() {
        RateLimiterConfig patientConfig = RateLimiterConfig.custom()
                .limitForPeriod(patientLimit)
                .limitRefreshPeriod(Duration.ofMinutes(1))
                .timeoutDuration(Duration.ZERO)
                .build();

        RateLimiterConfig providerConfig = RateLimiterConfig.custom()
                .limitForPeriod(providerLimit)
                .limitRefreshPeriod(Duration.ofMinutes(1))
                .timeoutDuration(Duration.ZERO)
                .build();

        RateLimiterConfig adminConfig = RateLimiterConfig.custom()
                .limitForPeriod(adminLimit)
                .limitRefreshPeriod(Duration.ofMinutes(1))
                .timeoutDuration(Duration.ZERO)
                .build();

        RateLimiterRegistry registry = RateLimiterRegistry.ofDefaults();
        registry.rateLimiter("patient", patientConfig);
        registry.rateLimiter("provider", providerConfig);
        registry.rateLimiter("admin", adminConfig);

        return registry;
    }
}

