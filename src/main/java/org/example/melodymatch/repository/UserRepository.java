package org.example.melodymatch.repository;

import org.example.melodymatch.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
}
