package com.example.controller;
import java.util.List;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.model.User;
import com.example.repository.UserRepo;
@RestController
@RequestMapping("/api/admin/users")
public class UserAdminController {
    private final UserRepo userRepo;
    public UserAdminController(UserRepo userRepo) {
        this.userRepo = userRepo;
    }
    @GetMapping
    public List<User> listUsers() {
        return userRepo.findAll();
    }
    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        Optional<User> u = userRepo.findById(id);
        return u.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User payload) {
        Optional<User> u = userRepo.findById(id);
        if (u.isEmpty()) return ResponseEntity.notFound().build();
        User user = u.get();
        if (payload.getEmail() != null) user.setEmail(payload.getEmail());
        if (payload.getRole() != null) user.setRole(payload.getRole());
        userRepo.save(user);
        return ResponseEntity.ok(user);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        Optional<User> u = userRepo.findById(id);
        if (u.isEmpty()) return ResponseEntity.notFound().build();
        userRepo.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
