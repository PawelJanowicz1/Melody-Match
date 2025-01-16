package org.example.melodymatch.system.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserEmailNotFoundException extends GenericAuthenticationException {

    public UserEmailNotFoundException() {
        super("User with given phoneNumber has not been found");
        log.warn("User with given phoneNumber has not been found");
    }
}
