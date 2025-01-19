package org.example.melodymatch.account;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.example.melodymatch.account.value_object.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "account")
@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
@NoArgsConstructor
@AllArgsConstructor
public class AccountModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private Long accountId;

    @Valid @Embedded
    private FirstName firstName;

    @Valid @Embedded
    private LastName lastName;

    @Valid @Embedded
    private Address address;

    @Valid @Embedded
    private PhoneNumber phoneNumber;

    @Valid @Embedded
    private UserEmail userEmail;

    @Valid @Embedded
    private EncodedPassword encodedPassword;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Role must not be null")
    private UserRole role;

    @Valid @Embedded
    private AccountLocked locked;

    @Valid @Embedded
    private Verified verified;

    @Column(name = "confirmation_token")
    private String confirmationToken;

    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "is_active")
    private boolean isActive = true;

}