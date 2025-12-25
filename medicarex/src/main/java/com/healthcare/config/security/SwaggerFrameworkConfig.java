package com.healthcare.config.security;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

//@Configuration
//@OpenAPIDefinition(
//        info = @Info(title = "Gopal Hospital API", version = "1.0"),
//        security = @SecurityRequirement(name = "ApiKeyAuth")
//)
//@SecurityScheme(
//        name = "ApiKeyAuth",
//        type = SecuritySchemeType.APIKEY,
//        in = SecuritySchemeIn.HEADER,
//        paramName = "X-API-KEY", // This MUST match request.getHeader("X-API-KEY")
//        description = "Please enter your Gopal Hospital Security Key."
//)
//public class SwaggerFrameworkConfig {
//}

@Configuration
@OpenAPIDefinition(
        info = @Info(title = "Gopal Hospital API", version = "1.0"),
        // This tells Swagger that the definition fetch itself requires this security scheme
        security = @SecurityRequirement(name = "ApiKeyAuth")
)
@SecurityScheme(
        name = "ApiKeyAuth",
        type = SecuritySchemeType.APIKEY,
        in = SecuritySchemeIn.HEADER,
        paramName = "X-API-KEY",
        description = "Enter your Gopal Hospital Security Key"
)
public class SwaggerFrameworkConfig {
}