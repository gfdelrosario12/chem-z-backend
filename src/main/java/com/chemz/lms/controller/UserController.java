package com.chemz.lms.controller;

import com.chemz.lms.model.*;
import com.chemz.lms.service.UserService;
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

    // --- Create based on role ---
    @PostMapping("/admin")
    public ResponseEntity<User> createAdmin(@RequestBody Admin admin) {
        return ResponseEntity.ok(userService.createUser(admin));
    }

    @PostMapping("/teacher")
    public ResponseEntity<User> createTeacher(@RequestBody Teacher teacher) {
        return ResponseEntity.ok(userService.createUser(teacher));
    }

    @PostMapping("/student")
    public ResponseEntity<User> createStudent(@RequestBody Student student) {
        return ResponseEntity.ok(userService.createUser(student));
    }

    // --- Get all ---
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // --- Get single ---
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // --- Delete ---
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
