package com.example.webSecurity;
import com.example.model.User;
import com.example.repository.UserRepo;
import com.example.service.RefreshTokenService;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
@Configuration
@EnableWebSecurity
public class SecurityConfig {
	@Autowired
	private JwtUtil jwtUtil;
	@Autowired
	private UserRepo userRepo;
	@Autowired
	private RefreshTokenService refreshTokenService;
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.csrf(csrf -> csrf.disable())
			.cors(cors -> {})
			.authorizeHttpRequests(auth -> auth
					.requestMatchers("/api/auth/**").permitAll()
					.requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()
					.requestMatchers("/oauth2/**").permitAll()
					.requestMatchers("/api/admin/**").hasRole("ADMIN")
					.anyRequest().authenticated()
			)
			.formLogin(login -> login.disable())
			.oauth2Login(oauth2 -> oauth2
				.successHandler((request, response, authentication) -> {
					Object principalObj = authentication.getPrincipal();
					Map<String, Object> attrs = null;
					if (principalObj != null) {
						if (principalObj instanceof java.util.Map) {
							@SuppressWarnings("unchecked")
							java.util.Map<String,Object> m = (java.util.Map<String,Object>) principalObj;
							attrs = m;
						} else {
							try {
								java.lang.reflect.Method getAttributes = null;
								try {
									getAttributes = principalObj.getClass().getMethod("getAttributes");
								} catch (NoSuchMethodException ignore) {
									try {
										getAttributes = principalObj.getClass().getMethod("getClaims");
									} catch (NoSuchMethodException ignore2) {
										getAttributes = null;
									}
								}
								if (getAttributes != null) {
									Object attrsObj = getAttributes.invoke(principalObj);
									if (attrsObj instanceof java.util.Map) {
										@SuppressWarnings("unchecked")
										java.util.Map<String,Object> m = (java.util.Map<String,Object>) attrsObj;
										attrs = m;
									}
								}
							} catch (Exception e) {
								attrs = null;
							}
						}
					}
					String username = null;
					String email = null;
					if (attrs != null) {
						username = (String) (attrs.getOrDefault("preferred_username", attrs.getOrDefault("email", "oidcuser")));
						email = (String) attrs.getOrDefault("email", null);
					}
					if (username == null) {
						username = "oidcuser" + System.currentTimeMillis();
					}
					User user = userRepo.findByUsername(username);
					if (user == null) {
						user = new User();
						user.setUsername(username);
						user.setEmail(email);
						user.setVerified(true);
						user.setPassword(passwordEncoder().encode(UUID.randomUUID().toString()));
						user.setRole("USER");
						userRepo.save(user);
					}
					String access = jwtUtil.generateAccessToken(user.getUsername(), user.getRole());
					com.example.model.RefreshToken refresh = refreshTokenService.createRefreshToken(user, jwtUtil.getRefreshExpirationMs());
					ResponseCookie accessCookie = ResponseCookie.from("ACCESS_TOKEN", access)
						.httpOnly(true)
						.secure(false)
						.path("/")
						.sameSite("Lax")
						.maxAge(jwtUtil.getAccessExpirationMs() / 1000)
						.build();
					ResponseCookie refreshCookie = ResponseCookie.from("REFRESH_TOKEN", refresh.getToken())
						.httpOnly(true)
						.secure(false)
						.path("/api/auth")
						.sameSite("Lax")
						.maxAge(jwtUtil.getRefreshExpirationMs() / 1000)
						.build();
					response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
					response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
					response.sendRedirect("/test-client/index.html");
				})
			);
		return http.build();
	}
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
	}
	@Bean
	public FilterRegistrationBean<RequestWrapperFilter> loggingFilter() {
		FilterRegistrationBean<RequestWrapperFilter> registrationBean = new FilterRegistrationBean<>();
		registrationBean.setFilter(new RequestWrapperFilter());
		return registrationBean;
	}
}
