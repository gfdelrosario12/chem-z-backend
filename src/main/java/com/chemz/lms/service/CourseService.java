package com.chemz.lms.service;

import com.chemz.lms.model.*;
import com.chemz.lms.repository.CourseRepository;
import com.chemz.lms.repository.EnrollmentRepository;
import com.chemz.lms.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;

    public CourseService(CourseRepository courseRepository, EnrollmentRepository enrollmentRepository, StudentRepository studentRepository) {
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.studentRepository = studentRepository;
    }

    // --- Create a course ---
    public Course createCourse(Course course) {
        // teacher must already be set in controller
        return courseRepository.save(course);
    }

    // --- Get all courses ---
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    // --- Get course by ID ---
    public Optional<Course> getCourseById(Long id) {
        return courseRepository.findById(id);
    }

    // --- Update course ---
    public Course updateCourse(Long id, String courseName, String description, Teacher teacher) {
        return courseRepository.findById(id)
                .map(course -> {
                    course.setCourseName(courseName);
                    course.setDescription(description);
                    course.setTeacher(teacher);
                    return courseRepository.save(course);
                }).orElseThrow(() -> new RuntimeException("Course not found"));
    }

    // --- Delete course ---
    public void deleteCourse(Long id) {
        // optionally delete enrollments as cascade or manually
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        courseRepository.delete(course);
    }

    // --- Enroll student ---
    public Enrollment enrollStudent(Student student, Course course) {
        Enrollment enrollment = new Enrollment(student, course);
        return enrollmentRepository.save(enrollment);
    }

    // --- Update grade for an enrollment ---
    public Enrollment updateGrade(Long enrollmentId, Double grade) {
        return enrollmentRepository.findById(enrollmentId)
                .map(enrollment -> {
                    enrollment.setGrade(grade);
                    return enrollmentRepository.save(enrollment);
                }).orElseThrow(() -> new RuntimeException("Enrollment not found"));
    }

    // --- Get students in a course ---
    public List<Student> getStudentsInCourse(Long courseId) {
        return enrollmentRepository.findByCourseId(courseId)
                .stream()
                .map(Enrollment::getStudent)
                .toList();
    }


    public long countCourses() {
        return courseRepository.count();
    }

    public Course saveCourse(Course course) {
        return courseRepository.save(course);
    }
    public List<Student> getStudentsForCourse(Long courseId) {
        return enrollmentRepository.findStudentsByCourseId(courseId);
    }
}
