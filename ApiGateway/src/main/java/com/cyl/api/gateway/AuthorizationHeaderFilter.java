package com.cyl.api.gateway;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import io.jsonwebtoken.Jwts;
import reactor.core.publisher.Mono;

@Component
public class AuthorizationHeaderFilter extends AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config> {

	@Autowired 
	Environment env;
	
	public AuthorizationHeaderFilter() {
		super(Config.class);
	}
	
	public static class Config{
		// put configuration properties here
	}
	
	@Override
	public GatewayFilter apply(Config config) {
		// below code return an anonymous GatewayFilter object with defined lamda funtion
		// the idea is to have the flexi to pass function around
		return (exchange, chain) -> {
			ServerHttpRequest request = exchange.getRequest();
			if(!request.getHeaders().containsKey("Authorization")) {
				return onError(exchange, "no authorization header", HttpStatus.UNAUTHORIZED);
			}
			String authorizationHeader = request.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
			String jwt = authorizationHeader.replaceAll("Bearer", "");
			
			if(!isJwtvalid(jwt)) {
				return onError(exchange, "jwt token is not valid", HttpStatus.UNAUTHORIZED);
			}
			
			return chain.filter(exchange);
		};
	}

	private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
	        ServerHttpResponse response = exchange.getResponse();
	        response.setStatusCode(httpStatus);        
		    return response.setComplete();
	}
	
	private boolean isJwtvalid(String jwt) {
		boolean returnValue =  true;
		String subject = Jwts.parser()
		.setSigningKey(env.getProperty("token.secret"))
		.parseClaimsJws(jwt).getBody().getSubject();
		
		if(subject == null || subject.isEmpty()) {
			returnValue = false;
		}
		
		return returnValue;
	}
	
	

}
