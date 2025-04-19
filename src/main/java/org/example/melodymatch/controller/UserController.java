package org.example.melodymatch.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.melodymatch.dto.LoginRequest;
import org.example.melodymatch.dto.LoginResponse;
import org.example.melodymatch.dto.RegisterDto;
import org.example.melodymatch.service.SongService;
import org.example.melodymatch.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final SongService songService;

    @PostMapping("/user/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody RegisterDto dto) {
        try {
            userService.registerUser(dto);
            return ResponseEntity.ok("User registered");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/admin/register")
    public ResponseEntity<String> registerAdmin(@Valid @RequestBody RegisterDto dto) {
        try {
            userService.registerAdmin(dto);
            return ResponseEntity.ok("Admin registered");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        String token = userService.login(request);
        return ResponseEntity.ok(new LoginResponse(token));
    }

    @GetMapping("/id")
    public ResponseEntity<Long> getUserId(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        return ResponseEntity.ok(songService.getUserIdFromToken(token));
    }
}