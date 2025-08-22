package com.chemz.lms.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

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
}
