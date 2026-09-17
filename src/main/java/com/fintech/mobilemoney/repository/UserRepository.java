package com.fintech.mobilemoney.repository;

import com.fintech.mobilemoney.model.User;
import org.springframework.data.repository.CrudRepository;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends CrudRepository<User, UUID> {
    // This lets us find the user after the database creates their ID
    Optional<User> findByPhoneNumber(String phoneNumber);
}