package org.example.melodymatch.controller;

import lombok.RequiredArgsConstructor;
import org.example.melodymatch.dto.UserDto;
import org.example.melodymatch.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserDto dto) {
        try {
            userService.register(dto);
            return ResponseEntity.ok("User registered");
        } catch(RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
