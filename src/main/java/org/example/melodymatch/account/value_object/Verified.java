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
public class Verified implements Serializable {

    @NotNull(message = "User verified property cannot be null")
    private boolean verified;

}