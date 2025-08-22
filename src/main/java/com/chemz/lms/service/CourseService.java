package com.chemz.lms.service;

import com.chemz.lms.model.*;
import com.chemz.lms.repository.CourseRepository;
import com.chemz.lms.repository.EnrollmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;

    public CourseService(CourseRepository courseRepository, EnrollmentRepository enrollmentRepository) {
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    // Create new course
    public Course createCourse(Course course) {
        return courseRepository.save(course);
    }

    // Enroll student in course
    public Enrollment enrollStudent(Student student, Course course) {
        Enrollment enrollment = new Enrollment(student, course, 0.0); // grade default 0
        return enrollmentRepository.save(enrollment);
    }

    // Update grade
    public Enrollment updateGrade(Long enrollmentId, Double grade) {
        return enrollmentRepository.findById(enrollmentId).map(e -> {
            e.setGrade(grade);
            return enrollmentRepository.save(e);
        }).orElseThrow(() -> new RuntimeException("Enrollment not found"));
    }

    // Get all courses
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    // Get students in a course
    public List<Enrollment> getStudentsInCourse(Long courseId) {
        return enrollmentRepository.findByCourseId(courseId);
    }
}
