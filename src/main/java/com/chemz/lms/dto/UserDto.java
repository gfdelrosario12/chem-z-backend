package com.chemz.lms.dto;

import com.chemz.lms.model.Admin;
import com.chemz.lms.model.Student;
import com.chemz.lms.model.Teacher;
import com.chemz.lms.model.User;

public class UserDto {
    private Long id;
    private String username;
    private String email;
    private String role;
    private String firstName;
    private String middleName;
    private String lastName;

    // Role-specific
    private String department;   // Admin
    private String subject;      // Teacher
    private String gradeLevel;   // Student

    public UserDto() {

    }
    // Constructor to map from entity
    public UserDto(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.role = user.getRole().toLowerCase(); // normalize
        this.firstName = user.getFirstName();
        this.middleName = user.getMiddleName();
        this.lastName = user.getLastName();

        // Role-specific mappings
        if (user instanceof Admin admin) {
            this.department = admin.getDepartment();
        } else if (user instanceof Teacher teacher) {
            this.subject = teacher.getSubject();
        } else if (user instanceof Student student) {
            this.gradeLevel = student.getGradeLevel();
        }
    }

    // --- Getters ---
    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public String getFirstName() { return firstName; }
    public String getMiddleName() { return middleName; }
    public String getLastName() { return lastName; }
    public String getDepartment() { return department; }
    public String getSubject() { return subject; }
    public String getGradeLevel() { return gradeLevel; }

    // --- Setters ---
    public void setId(Long id) { this.id = id; }
    public void setUsername(String username) { this.username = username; }
    public void setEmail(String email) { this.email = email; }
    public void setRole(String role) { this.role = role; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setMiddleName(String middleName) { this.middleName = middleName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setDepartment(String department) { this.department = department; }
    public void setSubject(String subject) { this.subject = subject; }
    public void setGradeLevel(String gradeLevel) { this.gradeLevel = gradeLevel; }
}
