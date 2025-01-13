package org.example.melodymatch.keycloak.service;

import org.example.melodymatch.keycloak.dto.UserRegistrationRecord;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.security.oauth2.jwt.Jwt;

public interface KeycloakUserService {

    UserRegistrationRecord createUser(UserRegistrationRecord userRegistrationRecord);
    UserRepresentation getUserByJwt(Jwt jwt);
    void deleteUserByJwt(Jwt jwt);
    void emailVerification(String userId);
    UserResource getUserResource(String userId);
    void updatePassword(Jwt jwt);
}
