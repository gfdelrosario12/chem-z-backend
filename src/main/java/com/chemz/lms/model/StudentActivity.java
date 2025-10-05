package com.chemz.lms.model;

import jakarta.persistence.*;

@Entity
@Table(name = "student_activities")
public class StudentActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activity_id")
    private Activity activity;

    private Integer score = 0;
    private Integer retries = 0;

    public StudentActivity() {}

    public StudentActivity(Student student, Activity activity) {
        this.student = student;
        this.activity = activity;
        this.score = 0;
        this.retries = 0;
    }

    // --- Getters and setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }

    public Activity getActivity() { return activity; }
    public void setActivity(Activity activity) { this.activity = activity; }

    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }

    public Integer getRetries() { return retries; }
    public void incrementRetries() { this.retries++; }

    @Transient
    public boolean isBlocked() {
        return this.retries >= 3;
    }
}
