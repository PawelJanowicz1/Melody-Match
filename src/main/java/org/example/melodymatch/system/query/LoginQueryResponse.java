package org.example.melodymatch.system.query;

import jakarta.validation.Valid;
import lombok.Getter;
import org.example.melodymatch.common.SelfValidating;
import org.example.melodymatch.system.value_object.Jwt;

@Getter
public final class LoginQueryResponse extends SelfValidating<LoginQueryResponse> {

    @Valid private final Jwt token;

    private LoginQueryResponse(Jwt token) {
        this.token = token;
        validateSelf();
    }

    public static LoginQueryResponse from(Jwt jwt) {
        return new LoginQueryResponse(jwt);
    }

    public record Json(String jwt) {
        public static Json fromQuery(LoginQueryResponse result) {
            return new Json(result.getToken().jwt());
        }
    }
}
