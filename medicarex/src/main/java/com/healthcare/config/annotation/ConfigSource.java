package com.healthcare.config.annotation;

import java.lang.annotation.*;

/**
 * Marks a @ConfigurationProperties class and indicates its configuration source files.
 * This makes it immediately clear where properties come from.
 *
 * Ex:
 * @ConfigSource(
 *     yamlFile = "application/mysql-db.yml",
 *     envOverrides = true,
 *     devConfigurable = true
 * )
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ConfigSource {

    /**
     * Primary YAML file in resources/application/ folder
     */
    String yamlFile() default "";

    /**
     * Multiple YAML files if configuration spans multiple files
     */
    String[] yamlFiles() default {};

    /**
     * Can be overridden in envs/{environment}/common.properties?
     */
    boolean envOverrides() default true;

    /**
     * Can developers override in envs/dev/{name}.properties?
     */
    boolean devConfigurable() default true;

    /**
     * Additional documentation
     */
    String description() default "";
}

