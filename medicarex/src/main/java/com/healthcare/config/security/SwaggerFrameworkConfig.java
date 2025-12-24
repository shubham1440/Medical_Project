package com.healthcare.config.security;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Gopal Hospital API",
                version = "1.0",
                description = "Security: Account locks after 3 failed attempts."
        ),
        // This MUST match the 'name' attribute in @SecurityScheme below
        security = @SecurityRequirement(name = "X-API-KEY")
)
@SecurityScheme(
        name = "X-API-KEY",               // The actual header key & the reference name
        type = SecuritySchemeType.APIKEY,
        in = SecuritySchemeIn.HEADER,
        description = "Enter your API Key. If you fail 3 times, contact admin@gopalhospital.com"
)
public class SwaggerFrameworkConfig {
}
