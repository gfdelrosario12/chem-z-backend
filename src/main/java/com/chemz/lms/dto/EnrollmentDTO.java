// EnrollmentDTO.java
package com.chemz.lms.dto;

public record EnrollmentDTO(
    Long id,
    Long studentId,
    String firstName,
    String lastName,
    Double grade
) {}
