package com.fintech.mobilemoney.service;

import com.fintech.mobilemoney.model.Transaction;
import com.fintech.mobilemoney.model.Wallet;
import com.fintech.mobilemoney.repository.WalletRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class WalletService {

    private final WalletRepository walletRepository;
    private final JdbcTemplate jdbcTemplate;

    public WalletService(WalletRepository walletRepository, JdbcTemplate jdbcTemplate) {
        this.walletRepository = walletRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    public BigDecimal getBalance(UUID walletId) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));
        return wallet.getBalance();
    }

    @Transactional
    public Transaction deposit(UUID walletId, BigDecimal amount, String idempotencyKey) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        if ("FROZEN".equals(wallet.getStatus())) {
            throw new RuntimeException("This wallet is FROZEN. Contact support.");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Amount must be greater than zero");
        }

        UUID txId = UUID.randomUUID();
        Timestamp now = Timestamp.valueOf(LocalDateTime.now());

        // The ::uuid cast forces PostgreSQL to accept the string as a UUID
        String sql = "INSERT INTO transactions (id, wallet_id, idempotency_key, type, amount, fee, status, description, created_at) " +
                     "VALUES (?::uuid, ?::uuid, ?, ?, ?, ?, ?, ?, ?)";
        
        jdbcTemplate.update(sql, txId.toString(), walletId.toString(), idempotencyKey, "DEPOSIT", amount, BigDecimal.ZERO, "SUCCESS", "DEPOSIT via Web Dashboard", now);

        wallet.setBalance(wallet.getBalance().add(amount));
        walletRepository.save(wallet);

        Transaction t = new Transaction();
        t.setId(txId);
        t.setAmount(amount);
        t.setFee(BigDecimal.ZERO);
        t.setStatus("SUCCESS");
        return t;
    }

    @Transactional
    public Transaction withdraw(UUID walletId, BigDecimal amount, String idempotencyKey) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        if ("FROZEN".equals(wallet.getStatus())) {
            throw new RuntimeException("This wallet is FROZEN. Contact support.");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Amount must be greater than zero");
        }

        BigDecimal fee = amount.multiply(new BigDecimal("0.015"));
        BigDecimal totalDeduction = amount.add(fee);

        if (wallet.getBalance().compareTo(totalDeduction) < 0) {
            throw new RuntimeException("Insufficient funds.");
        }

        UUID txId = UUID.randomUUID();
        Timestamp now = Timestamp.valueOf(LocalDateTime.now());

        String sql = "INSERT INTO transactions (id, wallet_id, idempotency_key, type, amount, fee, status, description, created_at) " +
                     "VALUES (?::uuid, ?::uuid, ?, ?, ?, ?, ?, ?, ?)";
        
        jdbcTemplate.update(sql, txId.toString(), walletId.toString(), idempotencyKey, "WITHDRAWAL", amount, fee, "SUCCESS", "WITHDRAWAL via Web Dashboard", now);

        wallet.setBalance(wallet.getBalance().subtract(totalDeduction));
        walletRepository.save(wallet);

        Transaction t = new Transaction();
        t.setId(txId);
        t.setAmount(amount);
        t.setFee(fee);
        t.setStatus("SUCCESS");
        return t;
    }
}