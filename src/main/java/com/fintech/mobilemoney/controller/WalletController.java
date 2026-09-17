package com.fintech.mobilemoney.controller;

import com.fintech.mobilemoney.model.Transaction;
import com.fintech.mobilemoney.service.WalletService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "*") // Allows the browser to talk to this backend
@RequestMapping("/api")
public class WalletController {

    private final WalletService walletService;

    // Spring automatically injects the WalletService we created earlier
    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    // 1. Read the customer's balance
    @GetMapping("/balance")
    public ResponseEntity<Map<String, Object>> getBalance(@RequestParam UUID walletId) {
        BigDecimal balance = walletService.getBalance(walletId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("walletId", walletId.toString());
        response.put("currentBalance", balance);
        return ResponseEntity.ok(response);
    }

    // 2. Make a Deposit
    @PostMapping("/deposit")
    public ResponseEntity<Map<String, Object>> deposit(
            @RequestParam UUID walletId,
            @RequestParam BigDecimal amount,
            @RequestParam String idempotencyKey) {

        // Process deposit and get the transaction receipt
        Transaction receipt = walletService.deposit(walletId, amount, idempotencyKey);
        
        // Get the updated balance after the transaction
        BigDecimal updatedBalance = walletService.getBalance(walletId);

        // Return the formatted receipt
        return ResponseEntity.ok(buildReceiptResponse(receipt, updatedBalance));
    }

    // 3. Make a Withdrawal
    @PostMapping("/withdraw")
    public ResponseEntity<Map<String, Object>> withdraw(
            @RequestParam UUID walletId,
            @RequestParam BigDecimal amount,
            @RequestParam String idempotencyKey) {

        // Process withdrawal (will throw error if insufficient funds)
        Transaction receipt = walletService.withdraw(walletId, amount, idempotencyKey);
        
        // Get the updated balance
        BigDecimal updatedBalance = walletService.getBalance(walletId);

        // Return the formatted receipt
        return ResponseEntity.ok(buildReceiptResponse(receipt, updatedBalance));
    }

    // Helper method to format the receipt exactly as your assignment requires
    private Map<String, Object> buildReceiptResponse(Transaction transaction, BigDecimal updatedBalance) {
        Map<String, Object> receipt = new HashMap<>();
        
        // THIS IS THE NEW LINE THAT SENDS THE TRANSACTION ID TO THE BROWSER
        receipt.put("transactionId", transaction.getId().toString()); 
        
        receipt.put("transactionType", transaction.getType());
        receipt.put("amount", transaction.getAmount());
        receipt.put("fee", transaction.getFee());
        receipt.put("updatedBalance", updatedBalance);
        receipt.put("status", transaction.getStatus());
        
        return receipt;
    }
}