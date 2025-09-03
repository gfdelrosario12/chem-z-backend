package com.chemz.lms.controller;

import com.chemz.lms.dto.UserDto;
import com.chemz.lms.dto.UserLoginDto;
import com.chemz.lms.model.User;
import com.chemz.lms.config.JwtUtil;
import com.chemz.lms.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public UserController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) {
        return ResponseEntity.ok(userService.createUser(user));
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserLoginDto loginRequest,
                                        HttpServletRequest request,
                                        HttpServletResponse response) {
        String ipAddress = request.getRemoteAddr();

        if (userService.validateLogin(loginRequest.getUsername(), loginRequest.getPassword(), ipAddress)) {
            // ✅ Generate JWT
            String token = jwtUtil.generateToken(loginRequest.getUsername());

            // ✅ Send as HttpOnly cookie
            Cookie cookie = new Cookie("jwt", token);
            cookie.setHttpOnly(true);
            cookie.setSecure(false); // set to true in production (HTTPS)
            cookie.setPath("/");
            cookie.setMaxAge((int) (jwtUtil.getExpirationTime() / 1000));
            response.addCookie(cookie);

            return ResponseEntity.ok("Login successful ✅");
        }
        return ResponseEntity.status(401).body("Invalid credentials ❌");
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletResponse response) {
        // ✅ Clear cookie
        Cookie cookie = new Cookie("jwt", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(0); // expire immediately
        response.addCookie(cookie);

        return ResponseEntity.ok("Logged out successfully 🚪");
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@CookieValue(name = "jwt", required = false) String token) {
        if (token == null || !jwtUtil.isTokenValid(token)) {
            return ResponseEntity.status(401).body("Unauthorized ❌");
        }

        String username = jwtUtil.extractUsername(token);
        User user = userService.getUserByUsername(username);

        // ✅ Return only safe fields
        return ResponseEntity.ok(new UserDto(user));
    }

}
