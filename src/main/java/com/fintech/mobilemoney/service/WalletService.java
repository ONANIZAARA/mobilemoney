package com.fintech.mobilemoney.service;

import com.fintech.mobilemoney.model.Transaction;
import com.fintech.mobilemoney.model.Wallet;
import com.fintech.mobilemoney.repository.TransactionRepository;
import com.fintech.mobilemoney.repository.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@Service
public class WalletService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    public WalletService(WalletRepository walletRepository, TransactionRepository transactionRepository) {
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public Transaction deposit(UUID walletId, BigDecimal amount, String idempotencyKey) {
        Wallet wallet = walletRepository.findByIdForUpdate(walletId)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        BigDecimal newBalance = wallet.getBalance().add(amount);
        walletRepository.updateBalance(walletId, newBalance);

        Transaction transaction = new Transaction();
        transaction.setId(UUID.randomUUID());
        transaction.setWalletId(walletId);
        transaction.setIdempotencyKey(idempotencyKey);
        transaction.setType("DEPOSIT");
        transaction.setAmount(amount);
        transaction.setFee(BigDecimal.ZERO);
        transaction.setStatus("SUCCESS");
        transaction.setDescription("Cash Deposit");
        
        return transactionRepository.save(transaction); // Returns the receipt!
    }

    @Transactional
    public Transaction withdraw(UUID walletId, BigDecimal amount, String idempotencyKey) {
        Wallet wallet = walletRepository.findByIdForUpdate(walletId)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        BigDecimal fee = amount.multiply(new BigDecimal("0.015")).setScale(4, RoundingMode.HALF_UP);
        BigDecimal totalDeduction = amount.add(fee);

        if (wallet.getBalance().compareTo(totalDeduction) < 0) {
            throw new RuntimeException("Insufficient funds. You need " + totalDeduction + " but only have " + wallet.getBalance());
        }

        BigDecimal newBalance = wallet.getBalance().subtract(totalDeduction);
        walletRepository.updateBalance(walletId, newBalance);

        Transaction transaction = new Transaction();
        transaction.setId(UUID.randomUUID());
        transaction.setWalletId(walletId);
        transaction.setIdempotencyKey(idempotencyKey);
        transaction.setType("WITHDRAWAL");
        transaction.setAmount(amount);
        transaction.setFee(fee);
        transaction.setStatus("SUCCESS");
        transaction.setDescription("Cash Withdrawal");
        
        return transactionRepository.save(transaction); // Returns the receipt!
    }
    
    // Helper to get current balance for the receipt
    public BigDecimal getBalance(UUID walletId) {
        return walletRepository.findById(walletId)
                .map(Wallet::getBalance)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));
    }
}