package com.chemz.lms.model;

import jakarta.persistence.*;

@Entity
@Table(name = "enrollments")
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Many Enrollments -> One Student
    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    // Many Enrollments -> One Course
    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    // Student's final grade in this course
    private Double grade;

    public Enrollment() {}

    public Enrollment(Student student, Course course, Double grade) {
        this.student = student;
        this.course = course;
        this.grade = grade;
    }

    public Enrollment(Student student, Course course) {
        this.student = student;
        this.course = course;
        this.grade = null; // or 0.0 if you prefer
    }

    // --- Getters & Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }

    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }

    public Double getGrade() { return grade; }
    public void setGrade(Double grade) { this.grade = grade; }
}
