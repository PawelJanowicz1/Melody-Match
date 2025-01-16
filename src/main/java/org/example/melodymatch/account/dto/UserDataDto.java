package org.example.melodymatch.account.dto;

import org.example.melodymatch.account.value_object.UserRole;

public record UserDataDto(UserRole appUserRole, String email) {
}
