package com.example.webSecurity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.service.TwoFactorAuthService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	@Autowired
	private TwoFactorAuthFilter twoFactorAuthFilter;
	
	
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.
			authorizeHttpRequests(auth -> auth
					.requestMatchers("/api/auth").permitAll()
					.anyRequest().authenticated()
					)
			.formLogin().disable()
			.addFilterBefore(twoFactorAuthFilter, UsernamePasswordAuthenticationFilter.class);
		return http.build();
	}
	

}
