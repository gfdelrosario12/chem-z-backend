package com.chemz.lms.controller;

import com.chemz.lms.dto.UserDto;
import com.chemz.lms.dto.UserLoginDto;
import com.chemz.lms.config.JwtUtil;
import com.chemz.lms.model.User;
import com.chemz.lms.service.EmailService;
import com.chemz.lms.service.EmailValidator;
import com.chemz.lms.service.UserService;
import com.chemz.lms.service.VerificationCodeService;
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
    private final EmailService emailService;
    private final VerificationCodeService verificationCodeService;

    public UserController(
            UserService userService,
            JwtUtil jwtUtil,
            EmailService emailService,
            VerificationCodeService verificationCodeService
    ) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.emailService = emailService;
        this.verificationCodeService = verificationCodeService;
    }

    // ===========================
    //        USER CRUD
    // ===========================

    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

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

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody UserLoginDto loginRequest,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        if (userService.validateLogin(loginRequest.getUsername(), loginRequest.getPassword())) {
            String token = jwtUtil.generateToken(loginRequest.getUsername());

            Cookie cookie = new Cookie("jwt", token);
            cookie.setHttpOnly(true);
            cookie.setSecure(false); // true on production
            cookie.setPath("/");
            cookie.setMaxAge((int) (jwtUtil.getExpirationTime() / 1000));
            response.addCookie(cookie);

            UserDto userDto = userService.getUserByUsername(loginRequest.getUsername());
            return ResponseEntity.ok(userDto);
        }

        return ResponseEntity.status(401).body("Invalid credentials");
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("jwt", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        return ResponseEntity.ok("Logged out successfully");
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(
            @CookieValue(name = "jwt", required = false) String token
    ) {
        if (token == null || !jwtUtil.isTokenValid(token)) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        String username = jwtUtil.extractUsername(token);
        UserDto userDto = userService.getUserByUsername(username);

        return ResponseEntity.ok(userDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(
            @PathVariable Long id,
            @RequestBody User updatedUser
    ) {
        UserDto result = userService.updateUser(id, updatedUser);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully");
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

            return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                    Map.of("error", "Failed to change password")
            );
        }
    }

    // ===========================
    //   EMAIL VERIFICATION
    // ===========================

    @PostMapping("/send-code")
    public ResponseEntity<?> sendCode(@RequestBody Map<String, String> body) {
        String email = body.get("email");

        if (email == null || email.isBlank()) {
            return ResponseEntity.badRequest().body("Email is required");
        }

        if (!EmailValidator.isValid(email)) {
            return ResponseEntity.badRequest().body("Invalid email format");
        }

        String code = verificationCodeService.generateCode(email);

        try {
            emailService.sendVerificationEmail(email, code);
            return ResponseEntity.ok(Map.of("message", "Verification code sent"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to send email");
        }
    }

    @PostMapping("/verify-code")
    public ResponseEntity<?> verifyCode(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String code = body.get("code");

        if (email == null || code == null) {
            return ResponseEntity.badRequest().body("Email and code required");
        }

        boolean isValid = verificationCodeService.verifyCode(email, code);

        if (!isValid) {
            return ResponseEntity.badRequest().body("Invalid or expired code");
        }

        return ResponseEntity.ok(Map.of("message", "Email verified successfully"));
    }
}
