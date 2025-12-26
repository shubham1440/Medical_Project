package com.gopal.medicarex.gateway_security.converter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

///**
// * Custom JWT Authentication Converter
// *
// * Extracts roles and scopes from Keycloak JWT tokens
// * Converts them to Spring Security GrantedAuthorities
// */
//@Slf4j
//@Component
//public class CustomJwtAuthenticationConverter implements Converter<Jwt, Mono<AbstractAuthenticationToken>> {
//
//    private static final String REALM_ACCESS_CLAIM = "realm_access";
//    private static final String ROLES_CLAIM = "roles";
//    private static final String RESOURCE_ACCESS_CLAIM = "resource_access";
//    private static final String SCOPE_CLAIM = "scope";
//
//    @Override
//    public Mono<AbstractAuthenticationToken> convert(Jwt jwt) {
//        Collection<GrantedAuthority> authorities = extractAuthorities(jwt);
//
//        log.debug("Extracted authorities for user {}: {}",
//                jwt.getSubject(), authorities);
//
//        return Mono.just(new JwtAuthenticationToken(jwt, authorities));
//    }
//
//    /**
//     * Extract authorities from JWT claims
//     * Combines realm roles, resource roles, and scopes
//     */
//    private Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
//        Set<GrantedAuthority> authorities = new HashSet<>();
//
//        // Extract Realm Roles
//        authorities.addAll(extractRealmRoles(jwt));
//
//        // Extract Resource Roles
//        authorities.addAll(extractResourceRoles(jwt));
//
//        // Extract Scopes
//        authorities.addAll(extractScopes(jwt));
//
//        return authorities;
//    }
//
//    /**
//     * Extract realm-level roles from Keycloak JWT
//     */
//    private Collection<GrantedAuthority> extractRealmRoles(Jwt jwt) {
//        Map<String, Object> realmAccess = jwt.getClaimAsMap(REALM_ACCESS_CLAIM);
//
//        if (realmAccess != null && realmAccess.containsKey(ROLES_CLAIM)) {
//            @SuppressWarnings("unchecked")
//            List<String> roles = (List<String>) realmAccess.get(ROLES_CLAIM);
//
//            return roles.stream()
//                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
//                    .collect(Collectors.toSet());
//        }
//
//        return Collections.emptySet();
//    }
//
//    /**
//     * Extract resource-specific roles from Keycloak JWT
//     */
//    private Collection<GrantedAuthority> extractResourceRoles(Jwt jwt) {
//        Map<String, Object> resourceAccess = jwt.getClaimAsMap(RESOURCE_ACCESS_CLAIM);
//
//        if (resourceAccess == null) {
//            return Collections.emptySet();
//        }
//
//        Set<GrantedAuthority> authorities = new HashSet<>();
//
//        resourceAccess.forEach((resource, access) -> {
//            @SuppressWarnings("unchecked")
//            Map<String, Object> accessMap = (Map<String, Object>) access;
//
//            if (accessMap.containsKey(ROLES_CLAIM)) {
//                @SuppressWarnings("unchecked")
//                List<String> roles = (List<String>) accessMap.get(ROLES_CLAIM);
//
//                roles.forEach(role -> authorities.add(
//                        new SimpleGrantedAuthority("ROLE_" + resource.toUpperCase() + "_" + role.toUpperCase())
//                ));
//            }
//        });
//
//        return authorities;
//    }
//
//    /**
//     * Extract OAuth2 scopes from JWT
//     */
//    private Collection<GrantedAuthority> extractScopes(Jwt jwt) {
//        String scopeString = jwt.getClaimAsString(SCOPE_CLAIM);
//
//        if (scopeString != null && !scopeString.isEmpty()) {
//            return Arrays.stream(scopeString.split(" "))
//                    .map(scope -> new SimpleGrantedAuthority("SCOPE_" + scope))
//                    .collect(Collectors.toSet());
//        }
//
//        return Collections.emptySet();
//    }
//}


@Slf4j
@Component
@ConditionalOnProperty(name = "spring.security.oauth2.resourceserver.jwt.issuer-uri")
public class CustomJwtAuthenticationConverter
        implements Converter<Jwt, Mono<AbstractAuthenticationToken>> {

    @Override
    public Mono<AbstractAuthenticationToken> convert(Jwt jwt) {
        Collection<GrantedAuthority> authorities = extractAuthorities(jwt);
        log.debug("Extracted authorities for user {}: {}", jwt.getSubject(), authorities);
        return Mono.just(new JwtAuthenticationToken(jwt, authorities));
    }

    private Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
        Set<GrantedAuthority> authorities = new HashSet<>();

        Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
        if (realmAccess != null && realmAccess.containsKey("roles")) {
            @SuppressWarnings("unchecked")
            List<String> roles = (List<String>) realmAccess.get("roles");
            authorities.addAll(roles.stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                    .collect(Collectors.toSet()));
        }

        String scopeString = jwt.getClaimAsString("scope");
        if (scopeString != null && !scopeString.isEmpty()) {
            authorities.addAll(Arrays.stream(scopeString.split(" "))
                    .map(scope -> new SimpleGrantedAuthority("SCOPE_" + scope))
                    .collect(Collectors.toSet()));
        }

        return authorities;
    }
}


