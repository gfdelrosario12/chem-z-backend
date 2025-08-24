package com.chemz.lms.controller;

import com.chemz.lms.dto.UserLoginDto;
import com.chemz.lms.model.*;
import com.chemz.lms.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) {
        return ResponseEntity.ok(userService.createUser(user));
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserLoginDto loginRequest,
                                        HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        if (userService.validateLogin(loginRequest.getUsername(), loginRequest.getPassword(), ipAddress)) {
            return ResponseEntity.ok("Login successful ✅");
        }
        return ResponseEntity.status(401).body("Invalid credentials ❌");
    }
}