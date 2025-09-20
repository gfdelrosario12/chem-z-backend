// StudentDTO.java
package com.chemz.lms.dto;

public class StudentDTO {
    private Long id;
    private String firstName;
    private String lastName;

    public StudentDTO(Long id, String firstName, String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    // getters
    public Long getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
}
