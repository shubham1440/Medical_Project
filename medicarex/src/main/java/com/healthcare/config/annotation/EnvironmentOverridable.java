package com.healthcare.config.annotation;

import java.lang.annotation.*;

/**
 * Marks a field that can be overridden per environment.
 * Shows developers which properties they can customize.
 *
 * Ex:
 * @EnvironmentOverridable(
 *     dev = "jdbc:mysql://localhost:3306/dev_db",
 *     staging = "jdbc:mysql://staging-db:3306/staging_db",
 *     prod = "jdbc:mysql://prod-cluster:3306/prod_db"
 * )
 * private String url;
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnvironmentOverridable {

    /**
     * Example value for development environment
     */
    String dev() default "";

    /**
     * Example value for staging environment
     */
    String staging() default "";

    /**
     * Example value for production environment
     */
    String prod() default "";

    /**
     * Description of what this property controls
     */
    String description() default "";

    /**
     * Is this required in production?
     */
    boolean requiredInProd() default false;
}

