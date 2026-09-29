package com.fintech.mobilemoney.service;

import com.fintech.mobilemoney.model.Transaction;
import com.fintech.mobilemoney.model.Wallet;
import com.fintech.mobilemoney.repository.WalletRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AdminService {

    private final WalletRepository walletRepository;
    private final JdbcTemplate jdbcTemplate;

    public AdminService(WalletRepository walletRepository, JdbcTemplate jdbcTemplate) {
        this.walletRepository = walletRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Wallet> getAllWallets() {
        return (List<Wallet>) walletRepository.findAll();
    }

    public List<Transaction> getAllTransactions() {
        // Direct SQL query guarantees we read from the correct 'transactions' table
        String sql = "SELECT id, wallet_id, idempotency_key, type, amount, fee, status, description, created_at FROM transactions ORDER BY created_at DESC";
        
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Transaction t = new Transaction();
            t.setId(rs.getObject("id", UUID.class));
            t.setWalletId(rs.getObject("wallet_id", UUID.class));
            t.setIdempotencyKey(rs.getString("idempotency_key"));
            t.setType(rs.getString("type"));
            t.setAmount(rs.getBigDecimal("amount"));
            t.setFee(rs.getBigDecimal("fee"));
            t.setStatus(rs.getString("status"));
            t.setDescription(rs.getString("description"));
            
            // Safe date parsing to prevent any null pointer crashes
            var ts = rs.getTimestamp("created_at");
            t.setCreatedAt(ts != null ? ts.toLocalDateTime() : LocalDateTime.now());
            return t;
        });
    }

    public void freezeWallet(UUID walletId) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));
        wallet.setStatus("FROZEN");
        walletRepository.save(wallet);
    }

    public void unfreezeWallet(UUID walletId) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));
        wallet.setStatus("ACTIVE");
        walletRepository.save(wallet);
    }
}