package org.example.melodymatch.utils.auth;

import org.example.melodymatch.model.User;
import org.example.melodymatch.repository.UserRepository;
import org.example.melodymatch.utils.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public void registerUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.USER);
        userRepository.save(user);
    }

    public String loginUser(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found!"));
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid credentials!");
        }
        return jwtUtil.generateToken(user);
    }

    public void registerAdmin(User admin, String creatorUsername) {
        User creator = userRepository.findByUsername(creatorUsername)
                .orElseThrow(() -> new RuntimeException("Creator not found!"));
        if (!creator.getRole().equals(Role.HEAD_ADMIN)) {
            throw new RuntimeException("Only HEAD_ADMIN can create an ADMIN!");
        }
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        admin.setRole(Role.ADMIN);
        userRepository.save(admin);
    }

    public void initializeHeadAdmin(User headAdmin) {
        if (userRepository.existsByRole(Role.HEAD_ADMIN)) {
            throw new RuntimeException("HEAD_ADMIN already exists!");
        }
        headAdmin.setPassword(passwordEncoder.encode(headAdmin.getPassword()));
        headAdmin.setRole(Role.HEAD_ADMIN);
        userRepository.save(headAdmin);
    }
}