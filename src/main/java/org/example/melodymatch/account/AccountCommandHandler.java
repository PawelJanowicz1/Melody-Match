package org.example.melodymatch.account;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.melodymatch.account.command.SetPasswordCommand;
import org.example.melodymatch.account.value_object.EncodedPassword;
import org.example.melodymatch.system.exception.InvalidJwtException;
import org.example.melodymatch.system.exception.UserEmailNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
class AccountCommandHandler {

    private final AccountRepository accountRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    AccountModel handleSetPassword(SetPasswordCommand setPasswordCommand) {
        Optional<AccountModel> account = accountRepository.findByPhoneNumber_PhoneNumber(setPasswordCommand.getPhoneNumber().getPhoneNumber());
        if(account.isEmpty()){
            log.warn("No user is associated with this email");
            throw new UserEmailNotFoundException();
        }
        else if(!setPasswordCommand.getToken().equals(account.get().getConfirmationToken())) {
            log.warn("Token for setting password is incorrect");
            throw new InvalidJwtException();
        }
        String encodedPassword = bCryptPasswordEncoder.encode(setPasswordCommand.getPassword().getRawPassword());
        account.get().setEncodedPassword(new EncodedPassword(encodedPassword));
        account.get().setConfirmationToken(null);
        return accountRepository.save(account.get());
    }
}