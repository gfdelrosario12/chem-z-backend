package com.chemz.lms.dto;

import com.chemz.lms.model.User;

public class UserDto {
    private Long id;
    private String username;
    private String email;
    private String role;
    private String firstName;
    private String middleName;
    private String lastName;

    // Constructor to map from entity
    public UserDto(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.role = user.getRole().toLowerCase(); // normalize
        this.firstName = user.getFirstName();
        this.middleName = user.getMiddleName();
        this.lastName = user.getLastName();
    }

    // Getters
    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public String getFirstName() { return firstName; }
    public String getMiddleName() { return middleName; }
    public String getLastName() { return lastName; }
}
