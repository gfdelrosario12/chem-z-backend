package com.chemz.lms.service;

import com.chemz.lms.model.User;
import com.chemz.lms.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Create user with Argon2 hashed password
    public User createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    // Validate login (raw password vs hashed)
    public boolean validateLogin(String username, String rawPassword) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        return userOpt.isPresent() &&
                passwordEncoder.matches(rawPassword, userOpt.get().getPassword());
    }
}