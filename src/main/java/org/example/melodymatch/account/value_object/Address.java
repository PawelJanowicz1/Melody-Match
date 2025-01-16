package org.example.melodymatch.account.value_object;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.io.Serializable;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode
public class Address implements Serializable {

    @NotBlank(message = "Country cannot be blank")
    private String country;

    @NotBlank(message = "Region cannot be blank")
    private String region;

    @NotBlank(message = "City cannot be blank")
    private String city;

    @NotBlank(message = "Postal code cannot be blank")
    private String postal_code;

    @NotBlank(message = "Street name cannot be blank")
    private String streetName;

    @NotBlank(message = "Building number cannot be blank")
    private String buildingNumber;
    private String apartment;
}
