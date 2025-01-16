package org.example.melodymatch.account.command;

import jakarta.validation.Valid;
import lombok.Getter;
import org.example.melodymatch.account.value_object.PhoneNumber;
import org.example.melodymatch.account.value_object.RawPassword;
import org.example.melodymatch.common.SelfValidating;

@Getter
public final class SetPasswordCommand extends SelfValidating<SetPasswordCommand> {

    @Valid
    private final PhoneNumber phoneNumber;

    @Valid
    private final RawPassword password;

    private final String token;

    private SetPasswordCommand(Json json) {
        this.phoneNumber = new PhoneNumber(json.phoneNumber);
        this.password = new RawPassword(json.password);
        this.token = json.token;
        validateSelf();
    }

    public record Json(String phoneNumber, String password, String token) {
    }
}
