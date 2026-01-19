package com.example.webSecurity;
import java.security.Key;
import java.util.Date;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
@Component
public class JwtUtil {
	private final Key key;
	private final long accessExpirationMs;
	private final long refreshExpiratonMs;
	public JwtUtil(@Value("${jwt.secret}") String secret,
			@Value("${jwt.access-exp-ms}") long accessExpirationMs,
			@Value("${jwt.refresh-exp-ms}") long refreshExpirationMs) {
					this.key = Keys.hmacShaKeyFor(secret.getBytes());
					this.accessExpirationMs = accessExpirationMs;
					this.refreshExpiratonMs = refreshExpirationMs;
	}
	public Long getAccessExpirationMs() {
		return this.accessExpirationMs;
	}
	public Long getRefreshExpirationMs() {
		return this.refreshExpiratonMs;
	}
		public String generateAccessToken(String username, String role) {
			return Jwts.builder()
					.setSubject(username)
					.addClaims(Map.of("role", role))
					.setIssuedAt(new Date())
					.setExpiration(new Date(System.currentTimeMillis() + accessExpirationMs))
					.signWith(key, SignatureAlgorithm.HS256)
					.compact();
	}
		public String generateRefreshToken(String username) {
				return Jwts.builder()
						.setSubject(username)
						.setIssuedAt(new Date())
						.setExpiration(new Date(System.currentTimeMillis() + refreshExpiratonMs))
						.signWith(key, SignatureAlgorithm.HS256)
						.compact();
		}
		public String extractUsername(String token) {
			return Jwts.parserBuilder()
					.setSigningKey(key)
					.build()
					.parseClaimsJws(token)
					.getBody()
					.getSubject();
		}
		public boolean isTokenValid(String token) {
			try {
				Jwts.parserBuilder()
				.setSigningKey(key)
				.build()
				.parseClaimsJws(token);
				return true;
			} catch(Exception e) {
				return false;
			}
		}
		public String extractRole(String token) {
			return Jwts.parserBuilder().setSigningKey(key).build()
					.parseClaimsJws(token).getBody().get("role", String.class);
		}
}
