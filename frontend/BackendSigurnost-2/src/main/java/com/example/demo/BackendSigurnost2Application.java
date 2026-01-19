package com.example.demo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
@EnableJpaRepositories(basePackages = "com.example.repository")
@EntityScan(basePackages = "com.example.model")
@SpringBootApplication(scanBasePackages = "com.example")
public class BackendSigurnost2Application {
	public static void main(String[] args) {
		SpringApplication.run(BackendSigurnost2Application.class, args);
	}
}
