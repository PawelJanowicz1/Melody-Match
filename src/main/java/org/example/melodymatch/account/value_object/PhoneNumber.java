package org.example.melodymatch.account.value_object;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode
public class PhoneNumber implements Serializable {

    @NotBlank(message = "PhoneNumber name cannot be blank")
    @NotNull
    @Pattern(regexp = "\\d{9}", message = "Phone number must be a 9-digit string")
    private String phoneNumber;

}
