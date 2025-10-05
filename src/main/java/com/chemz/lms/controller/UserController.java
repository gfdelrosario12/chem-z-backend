package com.chemz.lms.controller;

import com.chemz.lms.dto.UserDto;
import com.chemz.lms.dto.UserLoginDto;
import com.chemz.lms.config.JwtUtil;
import com.chemz.lms.model.User;
import com.chemz.lms.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public UserController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    // ✅ Get all users
    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // ✅ Register
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            UserDto createdUser = userService.createUser(user);

            return ResponseEntity.ok(Map.of(
                    "message", "Account created successfully!",
                    "username", createdUser.getUsername(),
                    "user", createdUser
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("error", "Registration failed."));
        }
    }

    // ✅ Login
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginDto loginRequest,
                                   HttpServletRequest request,
                                   HttpServletResponse response) {
        String ipAddress = request.getRemoteAddr();

        if (userService.validateLogin(loginRequest.getUsername(), loginRequest.getPassword())) {
            String token = jwtUtil.generateToken(loginRequest.getUsername());

            Cookie cookie = new Cookie("jwt", token);
            cookie.setHttpOnly(true);
            cookie.setSecure(false); // ⚠️ true in production (HTTPS)
            cookie.setPath("/");
            cookie.setMaxAge((int) (jwtUtil.getExpirationTime() / 1000));
            response.addCookie(cookie);

            UserDto userDto = userService.getUserByUsername(loginRequest.getUsername());
            return ResponseEntity.ok(userDto);
        }

        return ResponseEntity.status(401).body("Invalid credentials ❌");
    }

    // ✅ Logout
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("jwt", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        return ResponseEntity.ok("Logged out successfully 🚪");
    }

    // ✅ Get current user
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@CookieValue(name = "jwt", required = false) String token) {
        if (token == null || !jwtUtil.isTokenValid(token)) {
            return ResponseEntity.status(401).body("Unauthorized ❌");
        }

        String username = jwtUtil.extractUsername(token);
        UserDto userDto = userService.getUserByUsername(username);

        return ResponseEntity.ok(userDto);
    }

    // ✅ Update user
    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
        UserDto result = userService.updateUser(id, updatedUser);
        System.out.println("✅ Updated user info: " + result);
        return ResponseEntity.ok(result);
    }


    // ✅ Delete user
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully 🗑️");
    }

    @PutMapping("/{id}/change-password")
    public ResponseEntity<?> changePassword(
            @PathVariable Long id,
            @RequestBody Map<String, String> requestBody
    ) {
        try {
            String oldPassword = requestBody.get("oldPassword");
            String newPassword = requestBody.get("newPassword");
            userService.changePassword(id, oldPassword, newPassword);
            return ResponseEntity.ok(Map.of("message", "Password changed successfully ✅"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to change password ❌"));
        }
    }

}
