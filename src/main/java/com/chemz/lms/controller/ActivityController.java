package com.chemz.lms.controller;

import com.chemz.lms.model.Activity;
import com.chemz.lms.model.Course;
import com.chemz.lms.service.ActivityService;
import com.chemz.lms.service.CourseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/activities")
public class ActivityController {
    private final ActivityService activityService;
    private final CourseService courseService;

    public ActivityController(ActivityService activityService, CourseService courseService) {
        this.activityService = activityService;
        this.courseService = courseService;
    }

    // GET all activities for a specific course
    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<Activity>> getActivitiesByCourse(@PathVariable Long courseId) {
        Course course = courseService.getCourseById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found with id " + courseId));

        List<Activity> activities = activityService.getActivitiesByCourse(course.getId());
        return ResponseEntity.ok(activities);
    }

    // POST create a new activity for a specific course
    @PostMapping("/course/{courseId}")
    public ResponseEntity<Activity> createActivity(
            @PathVariable Long courseId,
            @RequestBody Activity activityRequest
    ) {
        Course course = courseService.getCourseById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found with id " + courseId));

        Activity newActivity = activityService.createActivity(activityRequest, course);
        return ResponseEntity.ok(newActivity);
    }
}
