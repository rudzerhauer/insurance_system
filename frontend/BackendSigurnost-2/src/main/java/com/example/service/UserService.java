package com.example.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.model.RegistrationRequest;
import com.example.model.User;
import com.example.repository.UserRepo;
@Service
public class UserService {
	@Autowired
	private UserRepo userRepo;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private TwoFactorAuthService twoFactorAuthService;
	public User register(RegistrationRequest req) {
		if(req.getUsername() == null || req.getEmail() == null || req.getPassword()==null) {
			throw new IllegalArgumentException("username, password and email are required");
		} 
		if(userRepo.findByUsername(req.getUsername())!=null) {
			throw new IllegalArgumentException("Username already exists");
		}
		if(userRepo.findByEmail(req.getEmail())!=null) {
			throw new IllegalArgumentException("Email already registered");
		}
		User user = new User();
		user.setUsername(req.getUsername());
		user.setEmail(req.getEmail());
		user.setPassword(passwordEncoder.encode(req.getPassword()));
		user.setRole("KLIJENT");
		user.setVerified(false);
		user = userRepo.save(user);
		this.twoFactorAuthService.generateAndSendCode(user);
		return user;
	}
	public User authenticate(String username, String password) {
		User user = userRepo.findByUsername(username);
		if(user != null && passwordEncoder.matches(password, user.getPassword())) {
			return user;
		}
		return null;
	}
}
