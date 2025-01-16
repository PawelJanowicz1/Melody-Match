package org.example.melodymatch.system.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InvalidPasswordForGivenEmailException extends GenericAuthenticationException {

    public InvalidPasswordForGivenEmailException() {
        super("Invalid code for given phone number");
        log.warn("Invalid code for given phone number");
    }
}
