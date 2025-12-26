package com.gopal.medicarex.gateway_security.config;

import com.gopal.medicarex.gateway_security.converter.CustomJwtAuthenticationConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.oidc.web.server.logout.OidcClientInitiatedServerLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;
import org.springframework.security.web.server.header.XFrameOptionsServerHttpHeadersWriter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

///**
// * Spring Security Configuration for Zero-Trust Gateway
// *
// * Implements three pillars:
// * 1. Integrity: mTLS at transport layer
// * 2. Identity: OAuth2/OIDC authentication
// * 3. Context: Role-based authorization
// */
//@Configuration
//@EnableWebFluxSecurity
//public class SecurityConfig {
//
//    private final CustomJwtAuthenticationConverter jwtAuthenticationConverter;
//
//    public SecurityConfig(CustomJwtAuthenticationConverter jwtAuthenticationConverter) {
//        this.jwtAuthenticationConverter = jwtAuthenticationConverter;
//    }
//
//    @Bean
//    public SecurityWebFilterChain securityWebFilterChain(
//            ServerHttpSecurity http,
//            ReactiveClientRegistrationRepository clientRegistrationRepository) {
//
//        http
//                // Authorize Exchange Configuration
//                .authorizeExchange(exchanges -> exchanges
//                        // Public Endpoints - No Authentication Required
//                        .pathMatchers(
//                                "/actuator/health",
//                                "/actuator/info",
//                                "/actuator/health/liveness",
//                                "/actuator/health/readiness"
//                        ).permitAll()
//
//                        // Prometheus Metrics - Require Authentication
//                        .pathMatchers("/actuator/prometheus").authenticated()
//
//                        // Finance Module - Requires FINANCE Role
//                        .pathMatchers("/api/finance/**")
//                        .hasRole("FINANCE")
//
//                        // EHR Module - Requires Medical Staff Roles
//                        .pathMatchers("/api/ehr/**")
//                        .hasAnyRole("DOCTOR", "NURSE", "ADMIN")
//
//                        // Beds Management - Multiple Staff Types
//                        .pathMatchers("/api/beds/**")
//                        .hasAnyRole("DOCTOR", "NURSE", "ADMIN", "RECEPTIONIST")
//
//                        // Partner External APIs - Requires Partner Scope
//                        .pathMatchers("/api/external/**")
//                        .hasAuthority("SCOPE_partner.api")
//
//                        // Admin Endpoints - Requires ADMIN Role
//                        .pathMatchers("/actuator/**")
//                        .hasRole("ADMIN")
//
//                        // All Other Requests Must Be Authenticated
//                        .anyExchange().authenticated()
//                )
//
//                // OAuth2 Login Configuration
//                .oauth2Login(oauth2 -> oauth2
//                        .authenticationSuccessHandler((webFilterExchange, authentication) -> {
//                            // Custom success handler logic
//                            return webFilterExchange.getExchange()
//                                    .getSession()
//                                    .doOnNext(session ->
//                                            session.getAttributes().put("authenticated", true)
//                                    )
//                                    .then();
//                        })
//                )
//
//                // OAuth2 Resource Server (JWT Validation)
//                .oauth2ResourceServer(oauth2 -> oauth2
//                        .jwt(jwt -> jwt
//                                .jwtAuthenticationConverter(jwtAuthenticationConverter)
//                        )
//                )
//
//                // Logout Configuration
//                .logout(logout -> logout
//                        .logoutUrl("/logout")
//                        .logoutSuccessHandler(oidcLogoutSuccessHandler(clientRegistrationRepository))
//                )
//
//                // Security Headers
//                .headers(headers -> headers
//                        .frameOptions(frameOptions -> frameOptions
//                                .mode(XFrameOptionsServerHttpHeadersWriter.Mode.DENY))
//                        .contentSecurityPolicy(csp -> csp
//                                .policyDirectives("default-src 'self'; frame-ancestors 'none';"))
//                )
//
//                // CSRF Configuration (Disabled for Stateless API)
//                .csrf(ServerHttpSecurity.CsrfSpec::disable);
//
//        return http.build();
//    }
//
//    /**
//     * OIDC Logout Success Handler
//     * Redirects to Identity Provider for complete logout
//     */
//    private ServerLogoutSuccessHandler oidcLogoutSuccessHandler(
//            ReactiveClientRegistrationRepository clientRegistrationRepository) {
//        OidcClientInitiatedServerLogoutSuccessHandler successHandler =
//                new OidcClientInitiatedServerLogoutSuccessHandler(clientRegistrationRepository);
//        successHandler.setPostLogoutRedirectUri("{baseUrl}");
//        return successHandler;
//    }
//}
//
//

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    /**
     * Basic Security Configuration (No OAuth2)
     * Used for development without Keycloak
     */
    @Bean
    @ConditionalOnProperty(name = "spring.security.oauth2.client.registration.keycloak.client-id", matchIfMissing = true, havingValue = "")
    public SecurityWebFilterChain basicSecurityFilterChain(ServerHttpSecurity http) {
        http
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/actuator/**", "/test/**").permitAll()
                        .anyExchange().permitAll()  // Allow all for development
                )
                .csrf(ServerHttpSecurity.CsrfSpec::disable);

        return http.build();
    }

    /**
     * Full OAuth2 Security Configuration
     * Used when Keycloak is configured
     */
    @Bean
    @ConditionalOnProperty(name = "spring.security.oauth2.client.registration.keycloak.client-id")
    public SecurityWebFilterChain oauth2SecurityFilterChain(
            ServerHttpSecurity http,
            CustomJwtAuthenticationConverter jwtAuthenticationConverter,
            ReactiveClientRegistrationRepository clientRegistrationRepository) {

        http
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/actuator/health", "/actuator/info").permitAll()
                        .pathMatchers("/api/finance/**").hasRole("FINANCE")
                        .pathMatchers("/api/ehr/**").hasAnyRole("DOCTOR", "NURSE", "ADMIN")
                        .pathMatchers("/api/beds/**").hasAnyRole("DOCTOR", "NURSE", "ADMIN", "RECEPTIONIST")
                        .pathMatchers("/api/external/**").hasAuthority("SCOPE_partner.api")
                        .anyExchange().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .authenticationSuccessHandler((webFilterExchange, authentication) ->
                                webFilterExchange.getExchange()
                                        .getSession()
                                        .doOnNext(session -> session.getAttributes().put("authenticated", true))
                                        .then()
                        )
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter))
                )
                .logout(logout -> logout
                        .logoutSuccessHandler(oidcLogoutSuccessHandler(clientRegistrationRepository))
                )
                .csrf(ServerHttpSecurity.CsrfSpec::disable);

        return http.build();
    }

    private ServerLogoutSuccessHandler oidcLogoutSuccessHandler(
            ReactiveClientRegistrationRepository clientRegistrationRepository) {
        OidcClientInitiatedServerLogoutSuccessHandler successHandler =
                new OidcClientInitiatedServerLogoutSuccessHandler(clientRegistrationRepository);
        successHandler.setPostLogoutRedirectUri("{baseUrl}");
        return successHandler;
    }
}

