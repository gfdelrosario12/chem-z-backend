package com.chemz.lms.service;

import com.chemz.lms.dto.UserDto;
import com.chemz.lms.model.*;
import com.chemz.lms.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // --- CREATE ---
    public UserDto createUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + user.getEmail());
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        Long lastId = userRepository.findTopByOrderByIdDesc()
                .map(User::getId)
                .orElse(0L);

        String generatedUsername = generateUsername(user.getRole(), lastId + 1);
        user.setUsername(generatedUsername);

        User saved = userRepository.save(user);

        UserDto dto = convertToDTO(saved);
        dto.setUsername(generatedUsername); // ensure it's present

        return dto;
    }

    // --- HELPER ---
    private String generateUsername(String role, Long id) {
        String roleCode;
        switch (role.toUpperCase()) {
            case "ADMIN": roleCode = "AD"; break;
            case "TEACHER": roleCode = "TC"; break;
            case "STUDENT": roleCode = "ST"; break;
            default: roleCode = "XX";
        }
        return String.format("CZ - %s - %02d", roleCode, id);
    }

    private UserDto convertToDTO(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setFirstName(user.getFirstName());
        dto.setMiddleName(user.getMiddleName());
        dto.setLastName(user.getLastName());

        if (user instanceof Admin admin) {
            dto.setDepartment(admin.getDepartment());
        } else if (user instanceof Teacher teacher) {
            dto.setSubject(teacher.getSubject());
        } else if (user instanceof Student student) {
            dto.setGradeLevel(student.getGradeLevel());
        }

        return dto;
    }

    // --- READ ---
    public List<UserDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<UserDto> getUserById(Long id) {
        return userRepository.findById(id).map(this::convertToDTO);
    }

    // --- UPDATE ---
    public UserDto updateUser(Long id, User updatedUser) {
        return userRepository.findById(id)
                .map(existingUser -> {
                    existingUser.setEmail(updatedUser.getEmail());
                    existingUser.setFirstName(updatedUser.getFirstName());
                    existingUser.setMiddleName(updatedUser.getMiddleName());
                    existingUser.setLastName(updatedUser.getLastName());

                    if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
                        existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
                    }

                    if (existingUser instanceof Admin admin && updatedUser instanceof Admin updatedAdmin) {
                        admin.setDepartment(updatedAdmin.getDepartment());
                    } else if (existingUser instanceof Teacher teacher && updatedUser instanceof Teacher updatedTeacher) {
                        teacher.setSubject(updatedTeacher.getSubject());
                    } else if (existingUser instanceof Student student && updatedUser instanceof Student updatedStudent) {
                        student.setGradeLevel(updatedStudent.getGradeLevel());
                    }

                    User saved = userRepository.save(existingUser);
                    return convertToDTO(saved);
                })
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // --- DELETE ---
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    // --- LOGIN ---
    public boolean validateLogin(String username, String rawPassword) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        return userOpt.isPresent() && passwordEncoder.matches(rawPassword, userOpt.get().getPassword());
    }

    public UserDto getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(this::convertToDTO)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
    }

    // --- COUNTS ---
    public long countUsers() { return userRepository.count(); }
    public long countAdmins() { return userRepository.countByRole("ADMIN"); }
    public long countStudents() { return userRepository.countByRole("STUDENT"); }

    // --- CHANGE PASSWORD ---
    public void changePassword(Long id, String oldPassword, String newPassword) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("Old password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}
