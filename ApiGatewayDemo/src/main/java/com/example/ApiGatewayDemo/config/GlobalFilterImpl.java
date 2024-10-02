package com.example.ApiGatewayDemo.config;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Component
@Order(1)
public class GlobalFilterImpl implements GlobalFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();
        HttpHeaders header = request.getHeaders();
            if (header.containsKey(HttpHeaders.AUTHORIZATION)) {
                String auth = header.getFirst(HttpHeaders.AUTHORIZATION);
                if (auth != null && auth.startsWith("Bearer ")) {
                    String token = auth.substring(7);
                    return chain.filter(exchange);
                } else {
                    return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                            "Authorization header is missing or invalid in request"));
                }
            } else {
                return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                        "Authorization header is missing in request"));
            }
        }

}

