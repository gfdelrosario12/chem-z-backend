package com.chemz.lms.repository;

import com.chemz.lms.model.Activity;
import com.chemz.lms.model.Course;
import com.chemz.lms.model.ActivityType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActivityRepository extends JpaRepository<Activity, Long> {
    List<Activity> findByCourseId(Long courseId);
    List<Activity> findByCourse(Course course);
    long countByCourseId(Long courseId);

    // New method to check uniqueness
    boolean existsByCourseIdAndTypeAndActivityNumber(Long courseId, ActivityType type, Integer activityNumber);
}
