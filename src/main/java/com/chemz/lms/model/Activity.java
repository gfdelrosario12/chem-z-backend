package com.chemz.lms.model;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
        name = "activities",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"course_id", "type", "activity_number"}
        )
)
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;

    @Enumerated(EnumType.STRING)
    private ActivityType type; // QUIZ or LAB

    private Integer activityNumber; // shared for Quiz/Lab

    private String fileUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    @OneToMany(mappedBy = "activity", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<StudentActivity> studentActivities = new HashSet<>();

    public Activity() {}

    public Activity(String title, String description, String fileUrl, ActivityType type, Course course) {
        this.title = title;
        this.description = description;
        this.fileUrl = fileUrl;
        this.type = type;
        this.course = course;
        this.activityNumber = null;
    }

    public Activity(String title, String description, String fileUrl, ActivityType type, Course course, Integer activityNumber) {
        this.title = title;
        this.description = description;
        this.fileUrl = fileUrl;
        this.type = type;
        this.course = course;
        this.activityNumber = activityNumber;
    }

    public Set<StudentActivity> getStudentActivities() { return studentActivities; }
    public void setStudentActivities(Set<StudentActivity> studentActivities) { this.studentActivities = studentActivities; }

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

    public Integer getActivityNumber() { return activityNumber; }
    public void setActivityNumber(Integer activityNumber) { this.activityNumber = activityNumber; }

    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }
}
