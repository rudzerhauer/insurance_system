package com.example.repository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.model.Policy;
public interface PolicyRepo extends JpaRepository<Policy, Long> {
			List<Policy> findByUserUsername(String username);
}
