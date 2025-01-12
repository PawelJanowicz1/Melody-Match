package org.example.melodymatch.repository;

import org.example.melodymatch.model.User;
import org.example.melodymatch.utils.Role;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByUsername(String username);
    boolean existsByRole(Role role);
}
