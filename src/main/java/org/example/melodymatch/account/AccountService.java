package org.example.melodymatch.account;

import org.example.melodymatch.account.value_object.AccountLocked;
import lombok.RequiredArgsConstructor;
import org.example.melodymatch.account.command.ClientRegisterCommand;
import org.example.melodymatch.account.value_object.*;
import org.example.melodymatch.common.DefaultResponse;
import org.example.melodymatch.common.sms.SmsService;
import org.example.melodymatch.common.utils.NumberGenerator;
import org.example.melodymatch.system.JwtService;
import org.example.melodymatch.system.LoginQueryHandler;
import org.example.melodymatch.system.exception.InvalidJwtException;
import org.example.melodymatch.system.query.LoginQueryResponse;
import org.example.melodymatch.system.query.VerifyQuery;
import org.example.melodymatch.system.value_object.Jwt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final JwtService jwtService;
    private final SmsService smsService;
    private final LoginQueryHandler loginQueryHandler;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AccountRepository accountRepository;

    public AccountModel getAccountFromToken(String token) throws InvalidJwtException {
        try {
            final var strippedToken = token.split(" ")[1];
            final var jwt = Jwt.from(strippedToken);
            final var phoneNumber = jwtService.extractUserPhoneNumberFromJwt(jwt).orElse(null);
            if (phoneNumber == null) {
                throw new InvalidJwtException();
            }
            final var optionalAccount = accountRepository.findByPhoneNumber_PhoneNumber(phoneNumber);
            return optionalAccount.orElseThrow(InvalidJwtException::new);
        } catch (InvalidJwtException e) {
            throw new InvalidJwtException();
        }
    }

    public DefaultResponse createClientAccount(ClientRegisterCommand clientRegisterCommand) {
        final var verificationCode = sendSms(clientRegisterCommand.getPhoneNumber().getPhoneNumber());
        saveAccountWithRole(clientRegisterCommand, String.valueOf(verificationCode), UserRole.CLIENT);
        smsService.sendSms(clientRegisterCommand.getPhoneNumber().getPhoneNumber(),
                "Twój kod weryfikacyjny to: " + verificationCode);
        return new DefaultResponse(true, "Client account was created.");
    }

    public DefaultResponse createAdminAccount(ClientRegisterCommand clientRegisterCommand) {
        final var verificationCode = sendSms(clientRegisterCommand.getPhoneNumber().getPhoneNumber());
        saveAccountWithRole(clientRegisterCommand, String.valueOf(verificationCode), UserRole.ADMIN);
        smsService.sendSms(clientRegisterCommand.getPhoneNumber().getPhoneNumber(),
                "Twój kod weryfikacyjny to: " + verificationCode);
        return new DefaultResponse(true, "Admin account was created.");
    }

    public int sendSms(final String phoneNumber) {
        final var verificationCode = NumberGenerator.generateNumber();
        smsService.sendSms(phoneNumber,
                "Twój kod weryfikacyjny to: " + verificationCode);
        return verificationCode;
    }

    private void saveAccountWithRole(ClientRegisterCommand clientRegisterCommand, String verificationCode, UserRole role) {
        final var account = new AccountModel();
        account.setFirstName(clientRegisterCommand.getFirstName());
        account.setLastName(clientRegisterCommand.getLastName());
        account.setAddress(clientRegisterCommand.getAddress());
        account.setPhoneNumber(clientRegisterCommand.getPhoneNumber());
        account.setUserEmail(clientRegisterCommand.getEmail());
        account.setEncodedPassword(new EncodedPassword(bCryptPasswordEncoder.encode(verificationCode)));
        account.setRole(role);
        account.setLocked(new AccountLocked(false));
        account.setVerified(new Verified(false));
        accountRepository.save(account);
    }

    public int sendSmsDTO(final String phoneNumber) {
        final var verificationCode = NumberGenerator.generateNumber();
        smsService.sendSms(phoneNumber,
                "Twój kod weryfikacyjny to: " + verificationCode);
        return verificationCode;
    }

    LoginQueryResponse verifyAccount(VerifyQuery verifyQuery, AccountModel accountModel) {
        final var loginQueryResponse = loginQueryHandler.handleLoginQuery(verifyQuery);
        verifyAccountInDatabase(accountModel);
        return loginQueryResponse;
    }

    public void login(final int verificationCode, final AccountModel accountModel) {
        accountModel.setEncodedPassword(new EncodedPassword(bCryptPasswordEncoder.encode(String.valueOf(verificationCode))));
        accountRepository.save(accountModel);
    }

    private void verifyAccountInDatabase(AccountModel accountModel) {
        accountModel.setVerified(new Verified(true));
        accountModel.setEncodedPassword(null);
        accountRepository.save(accountModel);
    }

    public AccountModel updateUserData(String bearerToken, ClientRegisterCommand clientRegisterCommand) throws InvalidJwtException {
        final var account = getAccountFromToken(bearerToken);
        if (clientRegisterCommand.getFirstName() != null) {
            account.setFirstName(clientRegisterCommand.getFirstName());
        }
        if (clientRegisterCommand.getLastName() != null) {
            account.setLastName(clientRegisterCommand.getLastName());
        }
        if (clientRegisterCommand.getAddress() != null) {
            final var currentAddress = account.getAddress();
            final var updatedAddress = new Address(
                    clientRegisterCommand.getAddress().getCountry() != null ? clientRegisterCommand.getAddress().getCountry() : currentAddress.getCountry(),
                    clientRegisterCommand.getAddress().getRegion() != null ? clientRegisterCommand.getAddress().getRegion() : currentAddress.getRegion(),
                    clientRegisterCommand.getAddress().getCity() != null ? clientRegisterCommand.getAddress().getCity() : currentAddress.getCity(),
                    clientRegisterCommand.getAddress().getPostal_code() != null ? clientRegisterCommand.getAddress().getPostal_code() : currentAddress.getPostal_code(),
                    clientRegisterCommand.getAddress().getStreetName() != null ? clientRegisterCommand.getAddress().getStreetName() : currentAddress.getStreetName(),
                    clientRegisterCommand.getAddress().getBuildingNumber() != null ? clientRegisterCommand.getAddress().getBuildingNumber() : currentAddress.getBuildingNumber(),
                    clientRegisterCommand.getAddress().getApartment() != null ? clientRegisterCommand.getAddress().getApartment() : currentAddress.getApartment()
            );
            account.setAddress(updatedAddress);
        }
        return accountRepository.save(account);
    }
}