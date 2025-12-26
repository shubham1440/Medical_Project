package com.gopal.medicarex.gateway_security.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@Component
public class GlobalLoggingFilter implements GlobalFilter, Ordered {

    private static final String TRACE_ID_HEADER = "X-Trace-Id";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        // Generate or extract trace ID
        String traceId = request.getHeaders().getFirst(TRACE_ID_HEADER);
        if (traceId == null) {
            traceId = UUID.randomUUID().toString();
        }

        // Add trace ID to response headers
        exchange.getResponse().getHeaders().add(TRACE_ID_HEADER, traceId);

        // Log request
        log.info("Incoming Request - TraceId: {}, Method: {}, Path: {}, Client: {}",
                traceId,
                request.getMethod(),
                request.getPath(),
                request.getRemoteAddress()
        );

        long startTime = System.currentTimeMillis();

        String finalTraceId = traceId;
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            long duration = System.currentTimeMillis() - startTime;

            log.info("Response - TraceId: {}, Status: {}, Duration: {}ms",
                    finalTraceId,
                    exchange.getResponse().getStatusCode(),
                    duration
            );
        }));
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}

