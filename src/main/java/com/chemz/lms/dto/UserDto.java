package com.chemz.lms.dto;

import com.chemz.lms.model.User;

public class UserDto {
    private Long id;
    private String username;
    private String email;
    private String role;

    // Constructor to map from entity
    public UserDto(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.role = user.getRole();
    }

    // Getters
    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
}
