package com.healthcare.config.properties;

import com.healthcare.config.annotation.ConfigSource;
import com.healthcare.config.annotation.DeveloperConfigurable;
import com.healthcare.config.annotation.EnvironmentOverridable;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Main Application Configuration Properties
 *
 * NEW DEVELOPER GUIDE:
 * - Base values: resources/application.yml
 * - Environment overrides: resources/envs/{environment}/common.properties
 * - Your overrides: resources/envs/dev/{your-name}.properties
 */
@ConfigSource(
        yamlFile = "application.yml",
        description = "Main application configuration including name, version, and environment settings"
)
@ConfigurationProperties(prefix = "app")
@Validated
@Data
public class ApplicationProperties {

    @NotBlank
    @EnvironmentOverridable(
            description = "Application name shown in logs and monitoring"
    )
    private String name;

    @NotBlank
    @EnvironmentOverridable(
            description = "Application version for tracking deployments"
    )
    private String version;

    @NotBlank
    @EnvironmentOverridable(
            dev = "dev",
            staging = "staging",
            prod = "prod",
            description = "Current environment: dev, staging, or prod",
            requiredInProd = true
    )
    private String environment;

    private Developer developer = new Developer();

    /**
     * Developer-specific settings (dev environment only)
     */
    @Data
    public static class Developer {

        @DeveloperConfigurable(
                example = "john",
                description = "Your name for personalized configuration"
        )
        private String name;

        @DeveloperConfigurable(
                example = "/home/john/workspace",
                description = "Your workspace directory for file operations",
                useCases = {"Temporary file storage", "Log output directory"}
        )
        private String workspace;

        @DeveloperConfigurable(
                example = "8081",
                description = "Your personal dev server port to avoid conflicts",
                useCases = {"Multiple developers on same machine", "Running multiple instances"}
        )
        private Integer port;
    }
}


