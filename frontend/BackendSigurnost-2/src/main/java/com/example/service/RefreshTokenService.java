package com.example.service;
import java.time.Instant;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.model.RefreshToken;
import com.example.model.User;
import com.example.repository.RefreshTokenRepo;
@Service
public class RefreshTokenService {
	@Autowired
	private RefreshTokenRepo refreshTokenRepo;
	public RefreshToken createRefreshToken(User user, long validityMs) {
			RefreshToken t = new RefreshToken();
			t.setToken(UUID.randomUUID().toString() + "." + UUID.randomUUID().toString());
			t.setUser(user);
			t.setExpiryDate(Instant.now().plusMillis(validityMs));
			return refreshTokenRepo.save(t);
	}
	public boolean isValid(RefreshToken rt) {
		return rt != null && rt.getExpiryDate().isAfter(Instant.now());
	}
	public void deleteByUser(User user) {
		refreshTokenRepo.deleteById(user.getId());
	}
}
