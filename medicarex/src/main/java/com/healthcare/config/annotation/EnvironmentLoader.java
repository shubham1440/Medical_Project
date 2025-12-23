package com.healthcare.config.annotation;

import java.lang.annotation.*;

/**
 * Marks a class as an environment-specific property loader.
 * Makes it clear which loader handles which environment.
 *
 * Ex:
 * @EnvironmentLoader(
 *     environment = "dev",
 *     priority = 1,
 *     description = "Loads dev environment with developer-specific overrides"
 * )
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@org.springframework.stereotype.Component
public @interface EnvironmentLoader {

    /**
     * Environment this loader supports (dev, staging, prod)
     */
    String environment();

    /**
     * Priority if multiple loaders could apply (lower = higher priority)
     */
    int priority() default 100;

    /**
     * Description of what this loader does
     */
    String description() default "";

    /**
     * Configuration loading strategy
     */
    String loadingStrategy() default "YAML + Environment Files + Developer Overrides";
}

