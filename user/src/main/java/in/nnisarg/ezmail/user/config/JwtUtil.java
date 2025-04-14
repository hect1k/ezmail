package in.nnisarg.ezmail.user.config;

import java.util.Date;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {
	@Value("${jwt.secret}")
	private String secret;

	@Value("${jwt.expiration}")
	private int expiration;

	public String generateToken(UUID userId) {
		SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());

		return Jwts.builder().subject(userId.toString()).issuedAt(new Date())
				.expiration(new Date(System.currentTimeMillis() + expiration)).signWith(key).compact();
	}

	public UUID extractUserId(String token) {
		SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());
		Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
		return UUID.fromString(claims.getSubject());
	}
}
