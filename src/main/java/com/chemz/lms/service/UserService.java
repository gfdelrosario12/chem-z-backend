package com.chemz.lms.service;

import com.chemz.lms.model.LoginLog;
import com.chemz.lms.model.User;
import com.chemz.lms.repository.LoginLogRepository;
import com.chemz.lms.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final LoginLogRepository loginLogRepository;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, LoginLogRepository loginLogRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.loginLogRepository = loginLogRepository;
    }

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

    public boolean validateLogin(String username, String rawPassword, String ipAddress) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        boolean success = userOpt.isPresent() && passwordEncoder.matches(rawPassword, userOpt.get().getPassword());

        // log attempt
        loginLogRepository.save(new LoginLog(username, success, ipAddress));

        return success;
    }
}
