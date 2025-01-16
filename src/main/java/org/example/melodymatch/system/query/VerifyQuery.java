package org.example.melodymatch.system.query;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.example.melodymatch.account.value_object.PhoneNumber;
import org.example.melodymatch.account.value_object.RawPassword;
import org.example.melodymatch.common.SelfValidating;

@Getter
public final class VerifyQuery extends SelfValidating<VerifyQuery> {

    @Valid
    @NotNull(message = "Phone number must not be null")
    private PhoneNumber phoneNumber;

    @Valid
    @NotNull(message = "Code must not be null")
    private RawPassword password;

    @JsonCreator
    public VerifyQuery(@JsonProperty("phoneNumber") PhoneNumber phoneNumber,
                        @JsonProperty("password") RawPassword password) {
        this.phoneNumber = phoneNumber;
        this.password = password;
        validateSelf();
    }

}