package org.example.melodymatch.system;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.melodymatch.account.AccountCredentials;
import org.example.melodymatch.account.AccountCredentialsRepository;
import org.example.melodymatch.system.exception.InvalidPasswordForGivenEmailException;
import org.example.melodymatch.system.exception.LockedAccountException;
import org.example.melodymatch.system.query.LoginQueryResponse;
import org.example.melodymatch.system.exception.*;
import org.example.melodymatch.system.query.VerifyQuery;
import org.example.melodymatch.system.value_object.Jwt;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoginQueryHandler {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AuthenticationManager authenticationManager;
    private final AccountCredentialsRepository accountCredentialsRepository;
    private final JwtService jwtService;

    public LoginQueryResponse handleLoginQuery(VerifyQuery verifyQuery) throws LockedAccountException {
        final var accountCredentials = getAccountCredentials(verifyQuery);
        log.info("User is trying to log in, userId: {}", accountCredentials.getAccountId());

        if(!isUserPasswordValid(verifyQuery, accountCredentials)) throw new InvalidPasswordForGivenEmailException();
        if(accountCredentials.isAccountLocked()) throw new LockedAccountException();
        log.info("User credentials are valid, userId: {}", accountCredentials.getAccountId());

        var jwt = generateJwtFromCredentials(accountCredentials);
        log.info("Successfully generated jwt for user, userId: {}", accountCredentials.getAccountId());

        authenticateToContext(verifyQuery.getPhoneNumber().getPhoneNumber(), verifyQuery.getPassword().getRawPassword());
        log.info("User has successfully logged in, userId: {}", accountCredentials.getAccountId());

        return LoginQueryResponse.from(jwt);
    }

    private Jwt generateJwtFromCredentials(AccountCredentials accountCredentials) {
        System.out.println("generateJwtFromCredentials");
        System.out.println(accountCredentials);
        return jwtService
                .generateJwt(accountCredentials)
                .orElseThrow(InvalidJwtException::new);
    }

    private AccountCredentials getAccountCredentials(VerifyQuery verifyQuery) {
        return accountCredentialsRepository
                .findByPhoneNumber_PhoneNumber(verifyQuery.getPhoneNumber().getPhoneNumber())
                .orElseThrow(UserEmailNotFoundException::new);
    }

    private void authenticateToContext(String email, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
    }

    private boolean isUserPasswordValid(VerifyQuery verifyQuery, AccountCredentials accountCredentials) {
        return bCryptPasswordEncoder.matches(
            verifyQuery.getPassword().getRawPassword(),
            accountCredentials.getEncodedPassword().getEncodedPassword());
    }
}
