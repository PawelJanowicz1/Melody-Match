package org.example.melodymatch.account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountCredentialsRepository extends JpaRepository<AccountCredentials, Integer> {
    @Query("SELECT a FROM AccountModel a WHERE a.phoneNumber.phoneNumber = :phoneNumber")
    Optional<AccountCredentials>findByPhoneNumber_PhoneNumber(String phoneNumber);
}