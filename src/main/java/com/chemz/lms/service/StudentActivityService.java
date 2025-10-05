package com.chemz.lms.service;

import com.chemz.lms.model.Activity;
import com.chemz.lms.model.Student;
import com.chemz.lms.model.StudentActivity;
import com.chemz.lms.repository.ActivityRepository;
import com.chemz.lms.repository.StudentActivityRepository;
import com.chemz.lms.repository.StudentRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentActivityService {

    private final StudentActivityRepository studentActivityRepository;
    private final StudentRepository studentRepository;
    private final ActivityRepository activityRepository;

    public StudentActivityService(StudentActivityRepository studentActivityRepository,
                                  StudentRepository studentRepository,
                                  ActivityRepository activityRepository) {
        this.studentActivityRepository = studentActivityRepository;
        this.studentRepository = studentRepository;
        this.activityRepository = activityRepository;
    }

    // Assign an activity to a student (creates StudentActivity)
    public StudentActivity assignActivityToStudent(Long studentId, Long activityId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new RuntimeException("Activity not found"));

        // Prevent duplicate
        return studentActivityRepository.findByStudentIdAndActivityId(studentId, activityId)
                .orElseGet(() -> studentActivityRepository.save(new StudentActivity(student, activity)));
    }

    // Get score for a student & activity
    public int getScore(Long studentId, Long activityId) {
        return studentActivityRepository.findByStudentIdAndActivityId(studentId, activityId)
                .map(StudentActivity::getScore)
                .orElseThrow(() -> new RuntimeException("Score not found for this student/activity"));
    }

    // Set/update score for a student & activity
    @Transactional
    public StudentActivity setScore(Long studentId, Long activityId, int score) {
        StudentActivity sa = studentActivityRepository.findByStudentIdAndActivityId(studentId, activityId)
                .orElseThrow(() -> new RuntimeException("StudentActivity not found"));

        sa.setScore(score);
        return studentActivityRepository.save(sa);
    }

    // Get retries
    public int getRetries(Long studentId, Long activityId) {
        return studentActivityRepository.findByStudentIdAndActivityId(studentId, activityId)
                .map(StudentActivity::getRetries)
                .orElseThrow(() -> new RuntimeException("StudentActivity not found"));
    }

    // Increment retries
    @Transactional
    public StudentActivity incrementRetries(Long studentId, Long activityId) {
        StudentActivity sa = studentActivityRepository.findByStudentIdAndActivityId(studentId, activityId)
                .orElseThrow(() -> new RuntimeException("StudentActivity not found"));

        if (sa.isBlocked()) {
            throw new RuntimeException("This activity is blocked for the student due to too many retries");
        }

        sa.incrementRetries();
        return studentActivityRepository.save(sa);
    }

    public double getAverageScore(Long studentId, Long courseId) {
        List<Integer> scores = studentActivityRepository.findScoresByStudentAndCourse(studentId, courseId);

        if (scores.isEmpty()) return 0.0;

        double sum = scores.stream().mapToDouble(Integer::doubleValue).sum();
        return sum / scores.size();
    }
}
