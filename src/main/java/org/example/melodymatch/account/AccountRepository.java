package org.example.melodymatch.account;

import org.example.melodymatch.account.value_object.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<AccountModel, Long> {

    @Query("SELECT a FROM AccountModel a WHERE a.phoneNumber.phoneNumber = :phoneNumber")
    Optional<AccountModel>findByPhoneNumber_PhoneNumber(String phoneNumber);

    List<AccountModel> findByRoleAndIsActiveTrue(UserRole role);

    void deleteByPhoneNumber_PhoneNumber(String phoneNumber);

}