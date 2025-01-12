package org.example.melodymatch.utils.auth;

import org.example.melodymatch.model.User;
import org.example.melodymatch.utils.auth.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register/user")
    public String registerUser(@RequestBody User user) {
        authService.registerUser(user);
        return "User registered successfully!";
    }

    @PostMapping("/register/admin")
    public String registerAdmin(@RequestBody User admin, @RequestParam String creator) {
        authService.registerAdmin(admin, creator);
        return "Admin registered successfully!";
    }

    @PostMapping("/register/head-admin")
    public String initializeHeadAdmin(@RequestBody User headAdmin) {
        authService.initializeHeadAdmin(headAdmin);
        return "HEAD_ADMIN initialized successfully!";
    }

    @PostMapping("/login")
    public String loginUser(@RequestParam String username, @RequestParam String password) {
        return authService.loginUser(username, password);
    }
}