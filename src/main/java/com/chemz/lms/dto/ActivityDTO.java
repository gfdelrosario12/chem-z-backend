package com.chemz.lms.dto;

import com.chemz.lms.model.ActivityType;

public class ActivityDTO {
    private Long id;
    private String title;
    private String description;
    private String fileUrl;
    private ActivityType type;
    private Integer activityNumber;
    private Integer score;

    // --- Getters ---
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getFileUrl() { return fileUrl; }
    public ActivityType getType() { return type; }
    public Integer getActivityNumber() { return activityNumber; } // ✅ FIXED
    public Integer getScore() { return score; }

    // --- Setters ---
    public void setId(Long id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }
    public void setType(ActivityType type) { this.type = type; }
    public void setActivityNumber(Integer activityNumber) { this.activityNumber = activityNumber; }
    public void setScore(Integer score) { this.score = score; }
}

