package org.example.melodymatch.account.dto;

import org.example.melodymatch.account.value_object.UserRole;
import org.example.melodymatch.common.ResponseStatus;

public record SignInResponse (ResponseStatus code,
                              String message,
                              String token,
                              UserRole appUserRole) {
}
