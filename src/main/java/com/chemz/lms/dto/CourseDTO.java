// CourseDTO.java
package com.chemz.lms.dto;

import java.util.List;

public class CourseDTO {
    private Long id;
    private String courseName;
    private String description;
    private TeacherDTO teacher;
    private List<StudentDTO> students;

    public CourseDTO(Long id, String courseName, String description, TeacherDTO teacher, List<StudentDTO> students) {
        this.id = id;
        this.courseName = courseName;
        this.description = description;
        this.teacher = teacher;
        this.students = students;
    }

    // --- getters ---
    public Long getId() { return id; }
    public String getCourseName() { return courseName; }
    public String getDescription() { return description; }
    public TeacherDTO getTeacher() { return teacher; }
    public List<StudentDTO> getStudents() { return students; }
}
