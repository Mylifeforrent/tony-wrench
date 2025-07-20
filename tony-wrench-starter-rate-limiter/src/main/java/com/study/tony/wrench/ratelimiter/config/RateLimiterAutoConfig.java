package com.study.tony.wrench.ratelimiter.config;

import com.study.tony.wrench.ratelimiter.aop.RateLimiterAOP;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RateLimiterAutoConfig {

    @Bean
    @ConditionalOnProperty(prefix = "tony.wrench.ratelimiter", name = "enabled", havingValue = "true", matchIfMissing = false)
    public RateLimiterAOP rateLimiterAOP() {
        return new RateLimiterAOP();
    }

}
