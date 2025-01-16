package org.example.melodymatch.account.command;

import jakarta.validation.Valid;
import lombok.Getter;
import org.example.melodymatch.account.value_object.UserEmail;
import org.example.melodymatch.account.value_object.UserRole;
import org.example.melodymatch.account.value_object.Username;
import org.example.melodymatch.common.SelfValidating;

@Getter
public class UserRegisterCommand extends SelfValidating<UserRegisterCommand> {

    @Valid
    private final UserEmail email;

    @Valid
    private final Username name;

    @Valid
    private final UserRole role;
    private UserRegisterCommand(Json json) {
        this.email = new UserEmail(json.email);
        this.name = new Username(json.name);
        this.role = json.role;
        validateSelf();
    }

    public record Json(String email, String name, UserRole role) {
    }
}
