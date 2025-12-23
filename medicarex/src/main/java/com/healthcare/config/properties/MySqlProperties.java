package com.healthcare.config.properties;

import com.healthcare.config.annotation.ConfigSource;
import com.healthcare.config.annotation.DeveloperConfigurable;
import com.healthcare.config.annotation.EnvironmentOverridable;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "db.mysql")
@Validated
@Data
@ConfigSource(
        yamlFiles = {"application/mysql-db.yml"},
        description = "Database connection configurations for MySQL"
)
public class MySqlProperties {


    @NotBlank
    @EnvironmentOverridable(
            dev = "jdbc:mysql://localhost:3306/dev_db",
            staging = "jdbc:mysql://staging-mysql.deloitte.internal:3306/staging_db",
            prod = "jdbc:mysql://prod-cluster.deloitte.internal:3306/prod_db",
            description = "MySQL JDBC connection URL",
            requiredInProd = true
    )
    @DeveloperConfigurable(
            example = "jdbc:mysql://localhost:3306/john_db",
            description = "Your personal MySQL database URL",
            useCases = {"Isolated dev database", "Local testing", "Different MySQL version"}
    )
    private String url;

    @NotBlank
    @EnvironmentOverridable(
            dev = "dev_user",
            staging = "staging_user",
            prod = "${DB_MYSQL_USER}",
            description = "MySQL username",
            requiredInProd = true
    )
    @DeveloperConfigurable(
            example = "john",
            description = "Your MySQL username"
    )
    private String username;

    @NotBlank
    @EnvironmentOverridable(
            dev = "dev_pass",
            staging = "staging_pass",
            prod = "${DB_MYSQL_PASSWORD}",
            description = "MySQL password (use env vars in prod!)",
            requiredInProd = true
    )
    @DeveloperConfigurable(
            example = "john123",
            description = "Your MySQL password"
    )
    private String password;

    @NotBlank
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
        private Long connectionTimeout = 30000L;
        private Long maxLifetime = 1800000L;
    }

    @Data
    public static class Jpa {
        private Hibernate hibernate = new Hibernate();
        private Boolean showSql = false;

        @Data
        public static class Hibernate {
            private String ddlAuto = "validate";
            private String dialect = "org.hibernate.dialect.MySQL8Dialect";
        }
    }
}
