package org.example.melodymatch.service;

import lombok.RequiredArgsConstructor;
import org.example.melodymatch.configs.JwtUtil;
import org.example.melodymatch.dto.LoginRequest;
import org.example.melodymatch.dto.LoginResponse;
import org.example.melodymatch.dto.RegisterDto;
import org.example.melodymatch.model.User;
import org.example.melodymatch.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    public User registerUser(RegisterDto dto) {
        if(userRepository.existsByUsername(dto.username())) {
            throw new RuntimeException("Username taken");
        }
        User user = new User();
        user.setUsername(dto.username());
        user.setPassword(encoder.encode(dto.password()));
        user.setEmail(dto.email());
        user.setRole("USER");
        return userRepository.save(user);
    }

    public String login(LoginRequest request) {
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));
        if(!encoder.matches(request.password(), user.getPassword())){
            throw new RuntimeException("Invalid credentials");
        }
        return JwtUtil.generateToken(user.getUsername(), user.getRole());
    }
}