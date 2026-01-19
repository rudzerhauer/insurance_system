package com.example.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.model.User;
public interface UserRepo extends JpaRepository<User,Long> {
	User findByUsername(String username);
	User findByPassword(String password);
	User findByVerificationCode(String verification_code);
	User findByEmail(String email);
}
