package com.healthcare.config.annotation;

import java.lang.annotation.*;

/**
 * Marks properties that individual developers can customize.
 * These go in envs/dev/{developer-name}.properties
 *
 * Ex:
 * @DeveloperConfigurable(
 *     example = "8081",
 *     description = "Your personal dev server port"
 * )
 * private Integer port;
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DeveloperConfigurable {

    /**
     * Example value for developer configuration
     */
    String example() default "";

    /**
     * Description of why a developer might want to customize this
     */
    String description() default "";

    /**
     * Common use cases for customization
     */
    String[] useCases() default {};
}

