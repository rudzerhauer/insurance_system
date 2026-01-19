package com.example.controller;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import com.example.model.PaymentRequest;
import com.example.model.Policy;
import com.example.model.Transaction;
import com.example.model.User;
import com.example.repository.PolicyRepo;
import com.example.repository.TransactionRepo;
import com.example.repository.UserRepo;
import com.example.service.Email;
import com.example.service.PdfService;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    @Autowired
    private TransactionRepo transactionRepo;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private PolicyRepo policyRepo;
    @Autowired
    private PdfService pdfService;
    @Autowired
    private Email emailService;
    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);
    @PostMapping("/create")
    public ResponseEntity<?> createPayment(@RequestBody PaymentRequest request) throws Exception {
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(request.getAmount())   
                .setCurrency(request.getCurrency())
                .build();
        PaymentIntent intent = PaymentIntent.create(params);
        return ResponseEntity.ok(Map.of(
                "clientSecret", intent.getClientSecret()
        ));
    }
    @PostMapping("/confirm")
    @Transactional
    public ResponseEntity<?> confirm(@RequestBody PaymentRequest request, Authentication auth) {
    	String username = auth != null ? auth.getName() : null;
        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Not authenticated"));
        }
        User user = userRepo.findByUsername(username);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "User not found"));
        }
        Policy template = policyRepo.findById(request.getPolicyId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Policy not found"));
        Policy purchasedPolicy;
        if (template.getUser() == null) {
            purchasedPolicy = clonePolicyForUser(template, user);
        } else {
            purchasedPolicy = clonePolicyForUser(template, user);
        }
        Transaction t = new Transaction();
        t.setUser(user);
        t.setPolicy(purchasedPolicy);
        t.setAmount(purchasedPolicy.getPrice());
        t.setProvider("Stripe");
        t.setSuccess("PENDING");
        t.setCreatedAt(LocalDateTime.now());
        t = transactionRepo.save(t);
        try {
            byte[] pdf = pdfService.generateInvoicePdf(t);
            emailService.sendInvoice(user.getEmail(), pdf);
            t.setSuccess("SUCCESS");
            transactionRepo.save(t);
        } catch (Exception ex) {
            logger.error("Failed to generate/send invoice for transaction {}: {}", t.getId(), ex.getMessage(), ex);
            t.setSuccess("FAILED");
            transactionRepo.save(t);
            return ResponseEntity.ok(Map.of(
                "message", "Payment recorded but email failed",
                "transactionId", t.getId(),
                "policyId", purchasedPolicy.getId(),
                "status", t.getSuccess(),
                "policy", purchasedPolicy
            ));
        }
        return ResponseEntity.ok(Map.of(
            "message", "Payment confirmed",
            "transactionId", t.getId(),
            "policyId", purchasedPolicy.getId(),
            "status", t.getSuccess(),
            "policy", purchasedPolicy
        ));
    }
    private Policy clonePolicyForUser(Policy template, User buyer) {
        Policy p = new Policy();
        p.setType(template.getType());
        p.setStartDate(template.getStartDate()); 
        p.setEndDate(template.getEndDate()); 
        p.setPrice(template.getPrice());
        p.setCreatedAt(LocalDate.now());
        p.setUser(buyer);
        return policyRepo.save(p);
    }
}