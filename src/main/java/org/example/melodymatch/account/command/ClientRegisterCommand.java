package org.example.melodymatch.account.command;


import org.example.melodymatch.account.value_object.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.example.melodymatch.account.value_object.FirstName;
import org.example.melodymatch.account.value_object.LastName;
import org.example.melodymatch.account.value_object.PhoneNumber;
import org.example.melodymatch.account.value_object.UserEmail;
import org.example.melodymatch.common.SelfValidating;

@Getter
@Valid
@NotNull
public class ClientRegisterCommand extends SelfValidating<ClientRegisterCommand> {

    @Valid
    @NotNull
    private final FirstName firstName;

    @Valid
    @NotNull
    private final LastName lastName;

    @Valid
    @NotNull
    private final Address address;

    @Valid
    @NotNull
    private final PhoneNumber phoneNumber;

    @Valid
    @NotNull
    private final UserEmail email;

    public ClientRegisterCommand(FirstName firstName, LastName lastName, PhoneNumber phoneNumber, UserEmail email, Address address) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.address = address;
        validateSelf();
    }

}
