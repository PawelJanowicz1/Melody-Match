package org.example.melodymatch.keycloak.service;

import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.example.melodymatch.keycloak.dto.UserRegistrationRecord;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.*;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class KeycloakUserServiceImpl implements KeycloakUserService{

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.urls.auth}")
    private String authServer;

    private Keycloak keycloak;

    public KeycloakUserServiceImpl(Keycloak keycloak) {
        this.keycloak = keycloak;
    }

    @Override
    public UserRegistrationRecord createUser(UserRegistrationRecord userRegistrationRecord) {

        UserRepresentation user=new UserRepresentation();
        user.setEnabled(true);
        user.setUsername(userRegistrationRecord.username());
        user.setEmail(userRegistrationRecord.email());
        user.setFirstName(userRegistrationRecord.firstName());
        user.setLastName(userRegistrationRecord.lastName());
        user.setEmailVerified(false);

        CredentialRepresentation credentialRepresentation=new CredentialRepresentation();
        credentialRepresentation.setValue(userRegistrationRecord.password());
        credentialRepresentation.setTemporary(false);
        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);

        List<CredentialRepresentation> list = new ArrayList<>();
        list.add(credentialRepresentation);
        user.setCredentials(list);

        UsersResource usersResource = getUsersResource();

        Response response = usersResource.create(user);

        if(Objects.equals(201,response.getStatus())){

            List<UserRepresentation> representationList = usersResource.searchByUsername(userRegistrationRecord.username(), true);
            if(!CollectionUtils.isEmpty(representationList)){
                UserRepresentation userRepresentation1 = representationList.stream().filter(userRepresentation -> Objects.equals(false, userRepresentation.isEmailVerified())).findFirst().orElse(null);
                assert userRepresentation1 != null;
                emailVerification(userRepresentation1.getId());
            }
            return  userRegistrationRecord;
        }


        return null;
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
    public void emailVerification(String userId){

        UsersResource usersResource = getUsersResource();
        usersResource.get(userId).sendVerifyEmail();
    }

    public UserResource getUserResource(String userId){
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
