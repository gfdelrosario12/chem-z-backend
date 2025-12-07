package com.chemz.lms.service;

import com.chemz.lms.model.ActivityType;
import com.chemz.lms.dto.ActivityDTO;
import com.chemz.lms.dto.StudentActivityDTO;
import com.chemz.lms.model.Activity;
import com.chemz.lms.model.StudentActivity;
import com.chemz.lms.model.Course;
import com.chemz.lms.repository.ActivityRepository;
import com.chemz.lms.repository.CourseRepository;
import com.chemz.lms.repository.StudentActivityRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final CourseRepository courseRepository;
    private final StudentActivityRepository studentActivityRepository;
    private final S3Service s3Service;

    private StudentActivityDTO toDTO(StudentActivity sa) {
        StudentActivityDTO dto = new StudentActivityDTO();
        dto.setId(sa.getId());
        dto.setStudentId(sa.getStudent().getId());
        dto.setActivityId(sa.getActivity().getId());
        dto.setScore(sa.getScore());
        dto.setRetries(sa.getRetries());
        dto.setBlocked(sa.isBlocked());
        return dto;
    }

    public ActivityService(ActivityRepository activityRepository,
                           CourseRepository courseRepository,
                           StudentActivityRepository studentActivityRepository,
                           S3Service s3Service) {
        this.activityRepository = activityRepository;
        this.courseRepository = courseRepository;
        this.studentActivityRepository = studentActivityRepository;
        this.s3Service = s3Service;
    }

    // --- New method ---
    public boolean existsByCourseAndTypeAndActivityNumber(Long courseId, ActivityType type, Integer activityNumber) {
        if (activityNumber == null || type == null) return false;
        return activityRepository.existsByCourseIdAndTypeAndActivityNumber(courseId, type, activityNumber);
    }

    // Create activity and assign to all students in the course
    @Transactional
    public ActivityDTO createActivity(Long courseId, Activity activityRequest) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        // Check for duplicate activity number
        if (activityRequest.getActivityNumber() != null && existsByCourseAndTypeAndActivityNumber(
                courseId, activityRequest.getType(), activityRequest.getActivityNumber())) {
            throw new IllegalArgumentException(
                    "Activity number " + activityRequest.getActivityNumber()
                            + " already exists for type " + activityRequest.getType() + " in this course."
            );
        }

        activityRequest.setCourse(course);
        Activity saved = activityRepository.save(activityRequest);

        // Assign to all students
        course.getEnrollments().forEach(enrollment -> {
            StudentActivity sa = new StudentActivity();
            sa.setActivity(saved);
            sa.setStudent(enrollment.getStudent());
            sa.setScore(0);
            studentActivityRepository.save(sa);
        });

        return mapToDTO(saved);
    }

    // Get all activities for a course
    public List<ActivityDTO> getActivitiesByCourse(Long courseId) {
        return activityRepository.findByCourseId(courseId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Map Activity entity to DTO (without student scores)
    private ActivityDTO mapToDTO(Activity activity) {
        String displayTitle = activity.getTitle();
        if (activity.getActivityNumber() != null && activity.getType() != null) {
            displayTitle = activity.getType().name() + " "
                    + activity.getActivityNumber() + ": "
                    + activity.getTitle();
        }

        ActivityDTO dto = new ActivityDTO();
        dto.setId(activity.getId());
        dto.setTitle(displayTitle);
        dto.setDescription(activity.getDescription());
        dto.setFileUrl(activity.getFileUrl());
        dto.setType(activity.getType());
        dto.setActivityNumber(activity.getActivityNumber());
        // No Activity-level score anymore
        return dto;
    }

    // Update activity metadata (title, description, type, etc.)
    public ActivityDTO updateActivity(Long id, Activity activityRequest) {
        Activity existing = activityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Activity not found"));

        existing.setTitle(activityRequest.getTitle());
        existing.setDescription(activityRequest.getDescription());
        existing.setType(activityRequest.getType());
        existing.setActivityNumber(activityRequest.getActivityNumber());
        existing.setFileUrl(activityRequest.getFileUrl());

        Activity saved = activityRepository.save(existing);
        return mapToDTO(saved);
    }

    @Transactional
    public void deleteActivity(Long id) {
        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Activity not found with id " + id));

        // Delete S3 file if exists
        if (activity.getFileUrl() != null && !activity.getFileUrl().isEmpty()) {
            String fileName = activity.getFileUrl().substring(activity.getFileUrl().lastIndexOf("/") + 1);
            s3Service.deleteFile(fileName);
        }

        activityRepository.delete(activity);
    }

    // ---------------- StudentActivity methods ----------------

    public int getScore(Long studentId, Long activityId) {
        return studentActivityRepository.findByStudentIdAndActivityId(studentId, activityId)
                .map(StudentActivity::getScore)
                .orElseThrow(() -> new RuntimeException("StudentActivity not found"));
    }

    @Transactional
    public StudentActivityDTO setScore(Long studentId, Long activityId, int score) {
        StudentActivity sa = studentActivityRepository.findByStudentIdAndActivityId(studentId, activityId)
                .orElseThrow(() -> new RuntimeException("StudentActivity not found"));

        sa.setScore(score);
        StudentActivity saved = studentActivityRepository.save(sa);

        return toDTO(saved);
    }

    @Transactional
    public StudentActivityDTO incrementRetries(Long studentId, Long activityId) {
        StudentActivity sa = studentActivityRepository.findByStudentIdAndActivityId(studentId, activityId)
                .orElseThrow(() -> new RuntimeException("StudentActivity not found"));

        if (sa.isBlocked()) {
            throw new RuntimeException("Activity blocked due to too many retries for this student");
        }

        sa.incrementRetries();
        StudentActivity saved = studentActivityRepository.save(sa);

        return toDTO(saved);
    }

    public long countAllActivities() {
        return activityRepository.count();
    }

    public int getRetries(Long studentId, Long activityId) {
        return studentActivityRepository.findByStudentIdAndActivityId(studentId, activityId)
                .map(StudentActivity::getRetries)
                .orElseThrow(() -> new RuntimeException("StudentActivity not found"));
    }

}
