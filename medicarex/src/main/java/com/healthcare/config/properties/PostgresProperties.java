package com.healthcare.config.properties;

import com.healthcare.config.annotation.DeveloperConfigurable;
import com.healthcare.config.annotation.EnvironmentOverridable;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "db.postgres")
@Data
public class PostgresProperties {

    @EnvironmentOverridable(
            dev = "jdbc:postgresql://localhost:5432/dev_db",
            staging = "jdbc:postgresql://staging-postgres.deloitte.internal:5432/staging_db",
            prod = "jdbc:postgresql://prod-cluster.deloitte.internal:5432/prod_db",
            description = "PostgreSQL JDBC connection URL"
    )
    @DeveloperConfigurable(
            example = "jdbc:postgresql://localhost:5432/sarah_db",
            description = "Your personal PostgreSQL database URL"
    )
    private String url;

    @EnvironmentOverridable(
            dev = "dev_user",
            description = "PostgreSQL username"
    )
    private String username;

    @EnvironmentOverridable(
            dev = "dev_pass",
            description = "PostgreSQL password"
    )
    private String password;

    private String driverClassName;

    private Pool pool = new Pool();
    private Jpa jpa = new Jpa();

    @Data
    public static class Pool {

        @EnvironmentOverridable(
                dev = "5",
                staging = "10",
                prod = "20",
                description = "Minimum number of connections in pool"
        )
        private Integer minSize = 5;

        @EnvironmentOverridable(
                dev = "10",
                staging = "30",
                prod = "50",
                description = "Maximum number of connections in pool"
        )
        private Integer maxSize = 20;
        private Long idleTimeout = 600000L;
    }

    @Data
    public static class Jpa {
        private Hibernate hibernate = new Hibernate();
        private Boolean showSql = false;

        @Data
        public static class Hibernate {
            private String ddlAuto = "validate";
            private String dialect = "org.hibernate.dialect.PostgreSQLDialect";
        }
    }
}

