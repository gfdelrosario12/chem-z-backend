package com.chemz.lms.model;

import com.chemz.lms.types.ActivityType;
import jakarta.persistence.*;

@Entity
@Table(name = "activities")
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;  // short title of activity

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column
    private String fileUrl;  // URL or path to uploaded file

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActivityType type; // QUIZ or ACTIVITY

    // Many Activities -> One Course
    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    public Activity() {}

    public Activity(String title, String description, String fileUrl, ActivityType type, Course course) {
        this.title = title;
        this.description = description;
        this.fileUrl = fileUrl;
        this.type = type;
        this.course = course;
    }

    // --- Getters & Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getFileUrl() { return fileUrl; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }

    public ActivityType getType() { return type; }
    public void setType(ActivityType type) { this.type = type; }

    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }
}
