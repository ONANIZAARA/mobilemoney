package com.fintech.mobilemoney.repository;

import com.fintech.mobilemoney.model.Transaction;
import org.springframework.data.repository.CrudRepository;
import java.util.UUID;

public interface TransactionRepository extends CrudRepository<Transaction, UUID> {
    // Basic save/find methods are automatically created here too.
}