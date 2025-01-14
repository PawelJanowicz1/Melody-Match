package org.example.melodymatch.keycloak.api;

import lombok.AllArgsConstructor;
import org.example.melodymatch.keycloak.dto.UserRegistrationRecord;
import org.example.melodymatch.keycloak.service.KeycloakUserService;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class KeycloakUserApi {


    private final KeycloakUserService keycloakUserService;


    @PostMapping("/create")
    public UserRegistrationRecord createUser(@RequestBody UserRegistrationRecord userRegistrationRecord) {

        return keycloakUserService.createUser(userRegistrationRecord);
    }

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password) {
        return keycloakUserService.login(username, password);
    }

    @GetMapping("/my-info")
    public UserRepresentation getUser(@AuthenticationPrincipal Jwt jwt) {
        return keycloakUserService.getUserByJwt(jwt);
    }

    @DeleteMapping("/delete")
    public void deleteUser(@AuthenticationPrincipal Jwt jwt) {
        keycloakUserService.deleteUserByJwt(jwt);
    }


    @PutMapping("/{userId}/send-verify-email")
    public void sendVerificationEmail(@PathVariable String userId) {
        keycloakUserService.emailVerification(userId);
    }

    @PutMapping("/update-password")
    public void updatePassword(@AuthenticationPrincipal Jwt jwt) {
        keycloakUserService.updatePassword(jwt);
    }
}
