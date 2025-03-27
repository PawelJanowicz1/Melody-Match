package org.example.melodymatch.service;

import lombok.RequiredArgsConstructor;
import org.example.melodymatch.dto.UserDto;
import org.example.melodymatch.model.User;
import org.example.melodymatch.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    public User register(UserDto dto) {
        if(userRepository.existsByUsername(dto.username())) {
            throw new RuntimeException("Username taken");
        }
        User user = new User();
        user.setUsername(dto.username());
        user.setPassword(encoder.encode(dto.password()));
        user.setEmail(dto.email());
        return userRepository.save(user);
    }
}