package com.healthcare.config.properties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;


//@ConfigurationProperties(prefix = "medicarex.security")
//@Validated
//@Data
public class SecurityProperties {

//    private Jwt jwt = new Jwt();
//    private Encryption encryption = new Encryption();
//
//    @Data
//    public static class Jwt {
//
//        @NotBlank
//        private String secret;
//
//        @NotNull
//        private Long expiration;
//
//        @NotNull
//        private Long refreshExpiration;
//    }
//
//    @Data
//    public static class Encryption {
//
//        @NotBlank
//        private String algorithm;
//
//        @NotBlank
//        private String key;
//
//        @NotNull
//        private Integer version;
//    }
}

