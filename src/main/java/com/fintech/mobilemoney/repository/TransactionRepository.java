package com.fintech.mobilemoney.repository;

import com.fintech.mobilemoney.model.Transaction;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface TransactionRepository extends CrudRepository<Transaction, UUID> {
    
    // This forces Java to look exactly at the 'transactions' table
    @Query("SELECT * FROM transactions")
    List<Transaction> findAllTransactions();
}