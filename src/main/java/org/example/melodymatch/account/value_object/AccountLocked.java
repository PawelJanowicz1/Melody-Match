package org.example.melodymatch.account.value_object;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.*;


import java.io.Serializable;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode
public class AccountLocked implements Serializable {

    @NotNull(message = "User locked property cannot be null")
    private boolean locked;

    public boolean isLocked() {
        return Boolean.TRUE.equals(locked);
    }

}