package com.healthcare.config.properties;


import com.healthcare.config.annotation.ConfigSource;
import com.healthcare.config.annotation.DeveloperConfigurable;
import com.healthcare.config.annotation.EnvironmentOverridable;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * Redis Cache Configuration Properties
 *
 * NEW DEVELOPER GUIDE:
 * - Base config: resources/application/redis.yml
 * - Override in: resources/envs/dev/{your-name}.properties
 *
 * DEVELOPER TIP:
 * Running multiple dev instances? Override redis.port in your properties file:
 *   redis.port=6380  # Your personal Redis instance
 */
@ConfigSource(
        yamlFile = "application/redis.yml",
        description = "Redis cache and session store configuration"
)
@ConfigurationProperties(prefix = "redis")
@Data
public class RedisProperties {

    @EnvironmentOverridable(
            dev = "localhost",
            staging = "staging-redis.deloitte.internal",
            prod = "prod-redis-cluster.deloitte.internal",
            description = "Redis server hostname"
    )
    @DeveloperConfigurable(
            example = "localhost",
            description = "Your Redis hostname (if running locally)",
            useCases = {"Local Redis instance", "Docker Redis container"}
    )
    private String host = "localhost";

    @EnvironmentOverridable(
            dev = "6379",
            staging = "6379",
            prod = "6379",
            description = "Redis server port"
    )
    @DeveloperConfigurable(
            example = "6380",
            description = "Your Redis port to avoid conflicts",
            useCases = {"Multiple Redis instances", "Non-standard port"}
    )
    private Integer port = 6379;

    @EnvironmentOverridable(
            dev = "",
            staging = "staging_redis_pass",
            prod = "${REDIS_PASSWORD}",
            description = "Redis password (empty for dev, env var for prod)",
            requiredInProd = true
    )
    private String password;
    private Integer database = 0;
    private Integer timeout = 5000;

    private Pool pool = new Pool();
    private Cluster cluster = new Cluster();

    @Data
    public static class Pool {
        private Integer maxActive = 8;
        private Integer maxIdle = 8;
        private Integer minIdle = 0;
        private Integer maxWait = -1;
    }

    @Data
    public static class Cluster {

        @EnvironmentOverridable(
                dev = "false",
                staging = "false",
                prod = "true",
                description = "Use Redis cluster mode"
        )
        private Boolean enabled = false;

        @EnvironmentOverridable(
                prod = "prod-redis1:7000,prod-redis2:7000,prod-redis3:7000",
                description = "Redis cluster node addresses"
        )
        private List<String> nodes;
    }
}

