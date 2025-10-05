package com.chemz.lms.dto;

public class CourseGradeDTO {
    private Long courseId;
    private String courseName;
    private double averageGrade;

    public CourseGradeDTO(Long courseId, String courseName, double averageGrade) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.averageGrade = averageGrade;
    }

    // --- Getters ---
    public Long getCourseId() { return courseId; }
    public String getCourseName() { return courseName; }
    public double getAverageGrade() { return averageGrade; }
}
