package com.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.model.LoginRequest;
import com.example.model.User;
import com.example.model.VerifyRequest;
import com.example.repository.UserRepo;
import com.example.service.TwoFactorAuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private TwoFactorAuthService twoFactorAuthService;
	@Autowired
	private UserRepo userRepo;
	
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody LoginRequest request) {
		Authentication auth = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(request.getUserName(), request.getPassword()));
		User user = userRepo.findByUsername(request.getUserName());
		String code = twoFactorAuthService.generateAndSendCode(user);
				
		return ResponseEntity.ok("Verification code sent to " + user.getEmail());
	}
	@PostMapping("/verify")
	public ResponseEntity<?> verify(@RequestBody VerifyRequest request) {
		if(twoFactorAuthService.verifyCode(request.getUsername(), request.getCode())) {
			return ResponseEntity.ok("2FA success");
		}
		return ResponseEntity.status(401).body("invalid code");
	}
}
