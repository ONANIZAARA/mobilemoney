package com.fintech.mobilemoney.repository;

import com.fintech.mobilemoney.model.Wallet;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public interface WalletRepository extends CrudRepository<Wallet, UUID> {

    // Find a wallet by the user's ID
    Optional<Wallet> findByUserId(UUID userId);

    // THE CRITICAL FINTECH QUERY:
    // "FOR UPDATE" tells PostgreSQL to lock this row so no other transaction can touch it.
    @Query("SELECT * FROM wallets WHERE id = :id FOR UPDATE")
    Optional<Wallet> findByIdForUpdate(@Param("id") UUID id);

    // Update the balance directly in the database
    @Modifying
    @Query("UPDATE wallets SET balance = :newBalance WHERE id = :id")
    int updateBalance(@Param("id") UUID id, @Param("newBalance") BigDecimal newBalance);
}