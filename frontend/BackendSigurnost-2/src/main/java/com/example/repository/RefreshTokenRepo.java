package com.example.repository;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.model.RefreshToken;
import com.example.model.User;
public interface RefreshTokenRepo extends JpaRepository<RefreshToken, Long> {
	Optional<RefreshToken> findByToken(String token);
	void deleteByUser(User user);
}
