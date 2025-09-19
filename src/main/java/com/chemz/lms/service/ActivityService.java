package com.chemz.lms.service;

import com.chemz.lms.model.Activity;
import com.chemz.lms.model.Course;
import com.chemz.lms.repository.ActivityRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActivityService {

    private final ActivityRepository activityRepository;

    public ActivityService(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    public Activity createActivity(Activity activity) {
        return activityRepository.save(activity);
    }

    public List<Activity> getActivitiesByCourse(Long courseId) {
        return activityRepository.findByCourseId(courseId);
    }

    public long countActivities() {
        return activityRepository.count();
    }
}
