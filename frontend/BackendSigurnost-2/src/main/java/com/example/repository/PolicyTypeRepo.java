package com.example.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.model.PolicyType;
public interface PolicyTypeRepo extends JpaRepository<PolicyType, Long> {
}
