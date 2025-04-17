package in.nnisarg.ezmail.gateway.filter;

import io.jsonwebtoken.Claims;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import in.nnisarg.ezmail.gateway.util.JwtUtil;

import java.util.Set;

@Component
public class JwtAuthFilter extends AbstractGatewayFilterFactory<JwtAuthFilter.Config> {

	private final JwtUtil jwtUtil;
	private static final Set<String> PUBLIC_PATHS = Set.of(
			"/emails/send",
			"/users/login",
			"/users/register");

	public JwtAuthFilter(JwtUtil jwtUtil) {
		super(Config.class);
		this.jwtUtil = jwtUtil;
	}

	@Override
	public GatewayFilter apply(Config config) {
		return (exchange, chain) -> {
			String path = exchange.getRequest().getPath().toString();

			if (PUBLIC_PATHS.stream().anyMatch(path::startsWith)) {
				return chain.filter(exchange);
			}

			String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

			if (authHeader == null || !authHeader.startsWith("Bearer ")) {
				exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
				return exchange.getResponse().setComplete();
			}

			try {
				String token = authHeader.substring(7);
				Claims claims = jwtUtil.parseJwt(token);

				ServerHttpRequest request = exchange.getRequest().mutate().header("X-User-ID", claims.getSubject()).build();

				return chain.filter(exchange.mutate().request(request).build());
			} catch (Exception e) {
				exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
				return exchange.getResponse().setComplete();
			}
		};
	}

	public static class Config {
	}
}
