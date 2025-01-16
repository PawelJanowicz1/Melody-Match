package org.example.melodymatch.account;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.example.melodymatch.common.ResponseStatus;
import lombok.RequiredArgsConstructor;
import org.example.melodymatch.account.command.ClientRegisterCommand;
import org.example.melodymatch.account.dto.SignInResponse;
import org.example.melodymatch.common.DefaultResponse;
import org.example.melodymatch.system.exception.InvalidJwtException;
import org.example.melodymatch.system.query.LoginQuery;
import org.example.melodymatch.system.query.LoginQueryResponse;
import org.example.melodymatch.system.query.VerifyQuery;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@RestController
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping("")
class AccountController {

    private final AccountRepository accountRepository;
    private final AccountService accountService;

    @Operation(summary = "Creates client with given data")
    @PostMapping("/account/create-client")
    ResponseEntity<Object> createClient(@Valid @NotNull @RequestBody ClientRegisterCommand clientRegisterCommand) {
        Optional<AccountModel> account = accountRepository.findByPhoneNumber_PhoneNumber(clientRegisterCommand.getPhoneNumber().getPhoneNumber());
        if (account.isPresent()) {
            return ResponseEntity.status(400)
                    .body(new SignInResponse(ResponseStatus.BAD_REQUEST, "USER_ALREADY_REGISTERED", null, null));
        }
        return ResponseEntity.ok().body(accountService.createClientAccount(clientRegisterCommand));
    }

    @Operation(summary = "Verification of client with code provided by SMS")
    @PostMapping("/account/verify")
    ResponseEntity<Object> verifyAccount(@Valid @NotNull @RequestBody VerifyQuery verifyQuery) {
        final var account = accountRepository.findByPhoneNumber_PhoneNumber(verifyQuery.getPhoneNumber().getPhoneNumber());
        if (account.isEmpty()) {
            return ResponseEntity.status(400)
                    .body(new SignInResponse(ResponseStatus.BAD_REQUEST, "USER_NOT_EXISTS", null, null));
        }
        if (account.get().getEncodedPassword() == null) {
            return ResponseEntity.status(400)
                    .body(new SignInResponse(ResponseStatus.BAD_REQUEST, "GENERATE_NEW_VERIFICATION_CODE", null, null));
        }
        final var responseJson = LoginQueryResponse.Json.fromQuery(accountService.verifyAccount(verifyQuery, account.get()));
        return ResponseEntity.ok().body(responseJson);
    }

    @Operation(summary = "Request to be logged in as existing user through phone number")
    @PostMapping("/account/login")
    ResponseEntity<Object> loginUser(@RequestBody LoginQuery loginQuery) {
        final var account = accountRepository.findByPhoneNumber_PhoneNumber(loginQuery.getPhoneNumber().getPhoneNumber());
        if (account.isEmpty()) {
            return ResponseEntity.status(400)
                    .body(new SignInResponse(ResponseStatus.BAD_REQUEST, "USER_NOT_EXISTS", null, null));
        }
        final var verificationCode = accountService.sendSms(account.get().getPhoneNumber().getPhoneNumber());
        accountService.login(verificationCode, account.get());
        return ResponseEntity.ok(new DefaultResponse(true, "Sms was sent."));
    }

    @GetMapping("/user/me")
    AccountModel getUserByBearerToken(@RequestHeader("Authorization") String bearerToken) {
        return accountService.getAccountFromToken(bearerToken);
    }

    @GetMapping("/user/get-user-by-phone-number")
    Optional<AccountModel> getUserByPhoneNumber(@RequestParam String phoneNumber) {
        return accountRepository.findByPhoneNumber_PhoneNumber(phoneNumber);
    }

    @PutMapping("/user/update-user-data")
    AccountModel updateUserData(
            @RequestHeader("Authorization") String bearerToken,
            @RequestBody ClientRegisterCommand clientRegisterCommand) {
        try {
            return accountService.updateUserData(bearerToken, clientRegisterCommand);
        } catch (InvalidJwtException e) {
            throw new InvalidJwtException();
        }
    }
}