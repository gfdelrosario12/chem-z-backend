package com.chemz.lms.dto;

import com.chemz.lms.types.ActivityType;

public class ActivityDTO {
    private Long id;
    private String title;
    private String description;
    private String fileUrl;
    private ActivityType type;
    private Integer quizNumber;

    public ActivityDTO(Long id, String title, String description, String fileUrl, ActivityType type, Integer quizNumber) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.fileUrl = fileUrl;
        this.type = type;
        this.quizNumber = quizNumber;
    }

    // --- Getters ---
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getFileUrl() { return fileUrl; }
    public ActivityType getType() { return type; }
    public Integer getQuizNumber() { return quizNumber; }
}
