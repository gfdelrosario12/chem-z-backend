package com.chemz.lms.service;

import com.chemz.lms.model.Activity;
import com.chemz.lms.model.Course;
import com.chemz.lms.repository.ActivityRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ActivityService {

    private final ActivityRepository activityRepository;

    public ActivityService(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    // Create activity and associate with a course
    public Activity createActivity(Activity activity, Course course) {
        activity.setCourse(course);
        return activityRepository.save(activity);
    }

    // Get all activities for a given course ID
    public List<Activity> getActivitiesByCourse(Long courseId) {
        return activityRepository.findByCourseId(courseId);
    }

    // Count all activities
    public long countActivities() {
        return activityRepository.count();
    }
}
