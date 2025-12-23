package com.healthcare.config.annotation;

import java.lang.annotation.*;

/**
 * Marks a module configuration class.
 * Documents what beans this module creates and what it depends on.
 *
 * Example:
 * @ConfigurationModule(
 *     name = "Database Module",
 *     provides = {"mysqlDataSource", "postgresDataSource"},
 *     dependsOn = {"DatabaseProperties"}
 * )
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@org.springframework.context.annotation.Configuration
public @interface ConfigurationModule {

    /**
     * Human-readable module name
     */
    String name();

    /**
     * Bean names this module provides
     */
    String[] provides() default {};

    /**
     * Configuration properties this module depends on
     */
    String[] dependsOn() default {};

    /**
     * Description of what this module does
     */
    String description() default "";

    /**
     * Module initialization order (lower = earlier)
     */
    int order() default 100;
}

