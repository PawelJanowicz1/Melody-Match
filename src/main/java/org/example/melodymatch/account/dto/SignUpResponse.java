package org.example.melodymatch.account.dto;

import org.example.melodymatch.common.*;
public record SignUpResponse (ResponseStatus code,
        String message,
        String token) {
}
