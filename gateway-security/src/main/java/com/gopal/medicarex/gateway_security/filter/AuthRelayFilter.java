package com.gopal.medicarex.gateway_security.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

//@Slf4j
//@Component
//public class AuthRelayFilter extends AbstractGatewayFilterFactory<AuthRelayFilter.Config> {
//
//    private final ServerOAuth2AuthorizedClientRepository authorizedClientRepository;
//
//    public AuthRelayFilter(ServerOAuth2AuthorizedClientRepository authorizedClientRepository) {
//        super(Config.class);
//        this.authorizedClientRepository = authorizedClientRepository;
//    }
//
//    @Override
//    public GatewayFilter apply(Config config) {
//        return (exchange, chain) -> exchange.getPrincipal()
//                .cast(Authentication.class)
//                .flatMap(authentication -> {
//                    if (authentication instanceof OAuth2AuthenticationToken oauth2Token) {
//                        return authorizedClientRepository
//                                .loadAuthorizedClient(
//                                        oauth2Token.getAuthorizedClientRegistrationId(),
//                                        authentication,
//                                        exchange
//                                )
//                                .map(OAuth2AuthorizedClient::getAccessToken)
//                                .map(token -> {
//                                    exchange.getRequest().mutate()
//                                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token.getTokenValue())
//                                            .build();
//
//                                    log.debug("Token relayed to downstream service");
//                                    return exchange;
//                                });
//                    }
//                    return Mono.just(exchange);
//                })
//                .flatMap(chain::filter);
//    }
//
//    public static class Config {
//        // Configuration properties if needed
//    }
//}

@Slf4j
@Component
@ConditionalOnProperty(name = "spring.security.oauth2.client.registration.keycloak.client-id")
public class AuthRelayFilter extends AbstractGatewayFilterFactory<AuthRelayFilter.Config> {

    private final ServerOAuth2AuthorizedClientRepository authorizedClientRepository;

    public AuthRelayFilter(ServerOAuth2AuthorizedClientRepository authorizedClientRepository) {
        super(Config.class);
        this.authorizedClientRepository = authorizedClientRepository;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> exchange.getPrincipal()
                .cast(Authentication.class)
                .flatMap(authentication -> {
                    if (authentication instanceof OAuth2AuthenticationToken oauth2Token) {
                        return authorizedClientRepository
                                .loadAuthorizedClient(
                                        oauth2Token.getAuthorizedClientRegistrationId(),
                                        authentication,
                                        exchange
                                )
                                .map(OAuth2AuthorizedClient::getAccessToken)
                                .map(token -> {
                                    exchange.getRequest().mutate()
                                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token.getTokenValue())
                                            .build();
                                    log.debug("Token relayed to downstream service");
                                    return exchange;
                                });
                    }
                    return Mono.just(exchange);
                })
                .flatMap(chain::filter);
    }

    public static class Config {
        // Configuration properties if needed
    }
}

