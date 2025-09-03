package com.chemz.lms.service;

import com.chemz.lms.model.Admin;
import com.chemz.lms.model.LoginLog;
import com.chemz.lms.model.Student;
import com.chemz.lms.model.Teacher;
import com.chemz.lms.model.User;
import com.chemz.lms.repository.LoginLogRepository;
import com.chemz.lms.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final LoginLogRepository loginLogRepository;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       LoginLogRepository loginLogRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.loginLogRepository = loginLogRepository;
    }

    // --- CREATE ---
    public User createUser(User user) {
        // Check email uniqueness
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + user.getEmail());
        }

        // Encode password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Generate username based on role + last id
        Long lastId = userRepository.findTopByOrderByIdDesc()
                .map(User::getId)
                .orElse(0L);

        String generatedUsername = generateUsername(user.getRole(), lastId + 1);
        user.setUsername(generatedUsername);

        return userRepository.save(user);
    }

    // --- HELPER ---
    private String generateUsername(String role, Long id) {
        String roleCode;
        switch (role.toUpperCase()) {
            case "ADMIN": roleCode = "AD"; break;
            case "TEACHER": roleCode = "TC"; break;
            case "STUDENT": roleCode = "ST"; break;
            default: roleCode = "XX"; // fallback
        }

        // Format: CZ - AD/TC/ST - 01
        return String.format("CZ - %s - %02d", roleCode, id);
    }

    // --- READ ---
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    // --- UPDATE ---
    public User updateUser(Long id, User updatedUser) {
        return userRepository.findById(id)
                .map(existingUser -> {
                    existingUser.setUsername(updatedUser.getUsername());
                    existingUser.setEmail(updatedUser.getEmail());
                    if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
                        existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
                    }

                    // Subclass-specific updates
                    if (existingUser instanceof Admin admin && updatedUser instanceof Admin updatedAdmin) {
                        admin.setDepartment(updatedAdmin.getDepartment());
                    } else if (existingUser instanceof Teacher teacher && updatedUser instanceof Teacher updatedTeacher) {
                        teacher.setSubject(updatedTeacher.getSubject());
                    } else if (existingUser instanceof Student student && updatedUser instanceof Student updatedStudent) {
                        student.setGradeLevel(updatedStudent.getGradeLevel());
                    }

                    return userRepository.save(existingUser);
                })
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // --- DELETE ---
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    // --- LOGIN VALIDATION ---
    public boolean validateLogin(String username, String rawPassword, String ipAddress) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        boolean success = userOpt.isPresent() && passwordEncoder.matches(rawPassword, userOpt.get().getPassword());

        loginLogRepository.save(new LoginLog(username, success, ipAddress));

        return success;
    }
}
