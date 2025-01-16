package org.example.melodymatch.account;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.melodymatch.account.value_object.EncodedPassword;
import org.example.melodymatch.account.value_object.AccountLocked;
import org.example.melodymatch.account.value_object.PhoneNumber;
import org.example.melodymatch.account.value_object.UserRole;

import java.io.Serializable;

@Entity
@Table(name = "account")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AccountCredentials implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private Long accountId;

    @Valid @Embedded
    private PhoneNumber phoneNumber;

    @Valid @Embedded
    private EncodedPassword encodedPassword;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Role must not be null")
    private UserRole role;

    @Valid @Embedded
    private AccountLocked accountLocked;

    public boolean isAccountLocked() {
        return this.accountLocked.isLocked();
    }
}