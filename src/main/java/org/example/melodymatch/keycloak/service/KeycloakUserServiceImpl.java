package org.example.melodymatch.keycloak.service;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Form;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.example.melodymatch.keycloak.dto.UserRegistrationRecord;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.*;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static com.stripe.Stripe.clientId;

@Service
@Slf4j
public class KeycloakUserServiceImpl implements KeycloakUserService {

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.urls.auth}")
    private String authServerUrl;

    @Value("${keycloak.adminClientId}")
    private String clientId;

    @Value("${keycloak.adminClientSecret}")
    private String clientSecret;

    private Keycloak keycloak;

    public KeycloakUserServiceImpl(Keycloak keycloak) {
        this.keycloak = keycloak;
    }

    @Override
    public UserRegistrationRecord createUser(UserRegistrationRecord userRegistrationRecord) {

        if (userRegistrationRecord.username() == null || userRegistrationRecord.username().isEmpty()) {
            throw new IllegalArgumentException("Username is required");
        }

        UsersResource usersResource = getUsersResource();

        log.info("Started creating user: {}", userRegistrationRecord.username());

        UserRepresentation user = new UserRepresentation();
        user.setEnabled(true);
        user.setUsername(userRegistrationRecord.username());
        user.setEmail(userRegistrationRecord.email());
        user.setFirstName(userRegistrationRecord.firstName());
        user.setLastName(userRegistrationRecord.lastName());
        user.setEmailVerified(true);

        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setValue(userRegistrationRecord.password());
        credentialRepresentation.setTemporary(false);
        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);

        user.setCredentials(Collections.singletonList(credentialRepresentation));

        Response response = usersResource.create(user);

        String responseBody = response.readEntity(String.class);
        log.info("Response from Keycloak: status = {}, body = {}", response.getStatus(), responseBody);

        if (response.getStatus() == 409) {
            log.error("Failed to create user: {}, details: {}", response.getStatus(), responseBody);
            throw new IllegalArgumentException("User already exists: " + responseBody);
        } else if (response.getStatus() != 201) {
            log.error("Failed to create user: {}, details: {}", response.getStatus(), responseBody);
            throw new RuntimeException("Failed to create user: " + responseBody);
        }

        String userId = usersResource.search(userRegistrationRecord.username()).get(0).getId();

        RoleRepresentation userRole = keycloak.realm(realm).roles().get("USER").toRepresentation();
        usersResource.get(userId).roles().realmLevel().add(Collections.singletonList(userRole));

        log.info("User {} was successfully created with ID: {}", userRegistrationRecord.username(), userId);

        return userRegistrationRecord;
    }

    @Override
    public String login(String username, String password) {
        try {
            Form form = new Form();
            form.param("grant_type", "password");
            form.param("client_id", clientId);
            form.param("client_secret", clientSecret);
            form.param("username", username);
            form.param("password", password);

            Response response = ClientBuilder.newClient()
                    .target(authServerUrl)
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED));

            if (response.getStatus() != 200) {
                String errorResponse = response.readEntity(String.class);
                log.error("Failed to get token: {} - {}", response.getStatus(), errorResponse);
                throw new RuntimeException("Failed to get token");
            }

            AccessTokenResponse tokenResponse = response.readEntity(AccessTokenResponse.class);
            return tokenResponse.getToken();
        } catch (Exception e) {
            log.error("Error during login: {}", e.getMessage(), e);
            throw new RuntimeException("Error during login", e);
        }
    }

    private UsersResource getUsersResource() {
        RealmResource realm1 = keycloak.realm(realm);
        return realm1.users();
    }

    @Override
    public UserRepresentation getUserByJwt(Jwt jwt) {
        String userId = jwt.getSubject();
        UserRepresentation user = getUsersResource().get(userId).toRepresentation();
        String name = jwt.getClaim("preferred_username");
        String givenName = jwt.getClaim("given_name");
        String familyName = jwt.getClaim("family_name");
        String email = jwt.getClaim("email");
        boolean emailVerified = jwt.getClaim("email_verified");

        user.setUsername(name);
        user.setFirstName(givenName);
        user.setLastName(familyName);
        user.setEmail(email);
        user.setEmailVerified(emailVerified);

        return user;
    }

    @Override
    public void deleteUserByJwt(Jwt jwt) {
        String userId = jwt.getSubject();
        getUsersResource().delete(userId);
    }


    @Override
    public void emailVerification(String userId) {

        UsersResource usersResource = getUsersResource();
        usersResource.get(userId).sendVerifyEmail();
    }

    public UserResource getUserResource(String userId) {
        UsersResource usersResource = getUsersResource();
        return usersResource.get(userId);
    }

    @Override
    public void updatePassword(Jwt jwt) {
        String userId = jwt.getSubject();
        UserResource userResource = getUserResource(userId);
        List<String> actions = new ArrayList<>();
        actions.add("UPDATE_PASSWORD");
        userResource.executeActionsEmail(actions);
    }

}
