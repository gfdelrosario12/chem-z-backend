package com.chemz.lms.service;

import com.chemz.lms.dto.ActivityDTO;
import com.chemz.lms.model.Activity;
import com.chemz.lms.model.Course;
import com.chemz.lms.repository.ActivityRepository;
import com.chemz.lms.repository.CourseRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final CourseRepository courseRepository;
    private final S3Service s3Service;

    public ActivityService(ActivityRepository activityRepository, CourseRepository courseRepository, S3Service s3Service) {
        this.activityRepository = activityRepository;
        this.courseRepository = courseRepository;
        this.s3Service = s3Service;
    }

    // Create activity and associate with a course
    public ActivityDTO createActivity(Long courseId, Activity activityRequest) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        activityRequest.setCourse(course);
        Activity saved = activityRepository.save(activityRequest);

        return mapToDTO(saved);
    }

    // Get all activities for a given course ID as DTOs
    public List<ActivityDTO> getActivitiesByCourse(Long courseId) {
        return activityRepository.findByCourseId(courseId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Count all activities
    public long countActivities() {
        return activityRepository.count();
    }

    // ✅ Utility method to map entity to DTO
    private ActivityDTO mapToDTO(Activity activity) {
        return new ActivityDTO(
                activity.getId(),
                activity.getTitle(),
                activity.getDescription(),
                activity.getFileUrl(),
                activity.getType(),
                activity.getQuizNumber()
        );
    }

    // Update activity
    public ActivityDTO updateActivity(Long id, Activity activityRequest) {
        Activity existing = activityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Activity not found"));

        existing.setTitle(activityRequest.getTitle());
        existing.setDescription(activityRequest.getDescription());
        existing.setType(activityRequest.getType());
        existing.setQuizNumber(activityRequest.getQuizNumber());
        existing.setFileUrl(activityRequest.getFileUrl());

        Activity saved = activityRepository.save(existing);
        return mapToDTO(saved);
    }

    public Optional<Activity> getActivityEntityById(Long id) {
        return activityRepository.findById(id);
    }

    @Transactional
    public void deleteActivity(Long id) {
        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Activity not found with id " + id));

        // Delete S3 file if exists
        if (activity.getFileUrl() != null && !activity.getFileUrl().isEmpty()) {
            // Extract the file name from the URL
            String fileName = activity.getFileUrl().substring(activity.getFileUrl().lastIndexOf("/") + 1);
            s3Service.deleteFile(fileName);
        }

        activityRepository.delete(activity);
    }
}
