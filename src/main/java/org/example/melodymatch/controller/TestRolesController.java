package org.example.melodymatch.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TestRolesController {

    @GetMapping("/user")
    public String helloUser() {
        return "Hello user!";
    }

    @GetMapping("/admin")
    public String helloAdmin() {
        return "Hello admin!";
    }
}
