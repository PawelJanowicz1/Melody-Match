package org.example.melodymatch.account.value_object;

import jakarta.persistence.Embeddable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode
public class RawPassword implements Serializable {

    @NotBlank(message = "Password cannot be blank")
    @Valid
    @NotNull
    private String rawPassword;

}