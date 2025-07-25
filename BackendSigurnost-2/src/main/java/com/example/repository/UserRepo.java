package com.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.model.User;

public interface UserRepo extends JpaRepository<User,Long> {

	User findByUsername(String username);
	User findByVerificationCode(String verificationCode);
}
