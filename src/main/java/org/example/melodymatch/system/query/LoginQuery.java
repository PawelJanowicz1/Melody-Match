package org.example.melodymatch.system.query;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.example.melodymatch.account.value_object.PhoneNumber;
import org.example.melodymatch.common.SelfValidating;

@Getter
public final class LoginQuery extends SelfValidating<LoginQuery> {

    @Valid
    @NotNull(message = "Phone number must not be null")
    private PhoneNumber phoneNumber;

    @JsonCreator
    public LoginQuery(@JsonProperty("phoneNumber") PhoneNumber phoneNumber) {
        this.phoneNumber = phoneNumber;
        validateSelf();
    }

}
