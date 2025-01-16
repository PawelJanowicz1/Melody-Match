package org.example.melodymatch.account.command;

import jakarta.validation.Valid;
import lombok.Getter;
import org.example.melodymatch.account.value_object.UserEmail;
import org.example.melodymatch.account.value_object.Username;
import org.example.melodymatch.common.SelfValidating;

@Getter
public final class HostRegisterCommand extends SelfValidating<HostRegisterCommand> {

    @Valid
    private final UserEmail email;

    @Valid
    private final Username name;

    private HostRegisterCommand(Json json) {
        this.email = new UserEmail(json.email);
        this.name = new Username(json.name);
        validateSelf();
    }

    public record Json(String email, String name) {
    }
}
