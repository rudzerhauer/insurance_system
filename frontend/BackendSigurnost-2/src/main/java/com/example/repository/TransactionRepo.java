package com.example.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.model.Transaction;
public interface TransactionRepo extends JpaRepository<Transaction, Long> {
}
