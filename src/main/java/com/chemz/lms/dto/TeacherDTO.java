// TeacherDTO.java
package com.chemz.lms.dto;

public class TeacherDTO {
    private Long id;
    private String firstName;
    private String lastName;

    public TeacherDTO(Long id, String firstName, String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    // getters
    public Long getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
}
