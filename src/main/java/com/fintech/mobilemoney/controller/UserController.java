package com.fintech.mobilemoney.controller;

import com.fintech.mobilemoney.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(
            @RequestParam String phoneNumber,
            @RequestParam String fullName,
            @RequestParam String pin) {

        UUID newWalletId = userService.registerUser(phoneNumber, fullName, pin);

        return ResponseEntity.ok(Map.of(
            "status", "SUCCESS",
            "message", "User registered and wallet created!",
            "walletId", newWalletId.toString()
        ));
    }

    // NEW LOGIN ENDPOINT
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(
            @RequestParam String phoneNumber,
            @RequestParam String pin) {

        UUID walletId = userService.loginUser(phoneNumber, pin);

        return ResponseEntity.ok(Map.of(
            "status", "SUCCESS",
            "message", "Login successful!",
            "walletId", walletId.toString()
        ));
    }
}