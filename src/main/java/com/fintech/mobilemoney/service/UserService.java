package com.fintech.mobilemoney.service;

import com.fintech.mobilemoney.model.User;
import com.fintech.mobilemoney.model.Wallet;
import com.fintech.mobilemoney.repository.UserRepository;
import com.fintech.mobilemoney.repository.WalletRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, WalletRepository walletRepository) {
        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Transactional
    public UUID registerUser(String phoneNumber, String fullName, String pin) {
        String hashedPin = passwordEncoder.encode(pin);

        User user = new User();
        user.setPhoneNumber(phoneNumber);
        user.setFullName(fullName);
        user.setPinHash(hashedPin);
        user.setKycStatus("VERIFIED");
        userRepository.save(user);

        User savedUser = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new RuntimeException("Failed to create user"));

        Wallet wallet = new Wallet();
        wallet.setUserId(savedUser.getId());
        wallet.setBalance(BigDecimal.ZERO);
        wallet.setCurrency("USD");
        wallet.setStatus("ACTIVE");
        walletRepository.save(wallet);

        Wallet savedWallet = walletRepository.findByUserId(savedUser.getId())
                .orElseThrow(() -> new RuntimeException("Failed to create wallet"));

        return savedWallet.getId(); 
    }

    // NEW LOGIN METHOD
    public UUID loginUser(String phoneNumber, String pin) {
        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new RuntimeException("User not found. Please register first."));
        
        if (!passwordEncoder.matches(pin, user.getPinHash())) {
            throw new RuntimeException("Invalid PIN. Please try again.");
        }
        
        Wallet wallet = walletRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Wallet not found"));
                
        return wallet.getId();
    }
}