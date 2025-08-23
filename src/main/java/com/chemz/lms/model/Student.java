package com.chemz.lms.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "students")
public class Student extends User {
    private String gradeLevel;

    public Student() {}

    public Student(String username, String password, String email, String gradeLevel) {
        super(username, password, email, "STUDENT");
        this.gradeLevel = gradeLevel;
    }

    public String getGradeLevel() { return gradeLevel; }
    public void setGradeLevel(String gradeLevel) { this.gradeLevel = gradeLevel; }

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Enrollment> enrollments = new HashSet<>();

    // --- Getters & helper methods ---
    public Set<Enrollment> getEnrollments() { return enrollments; }
    public void setEnrollments(Set<Enrollment> enrollments) { this.enrollments = enrollments; }

    /**
     * Convenience method to get all courses this student is enrolled in
     */
    public List<Course> getEnrolledCourses() {
        return enrollments.stream()
                .map(Enrollment::getCourse)
                .toList();
    }
}
