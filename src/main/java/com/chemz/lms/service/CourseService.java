package com.chemz.lms.service;

import com.chemz.lms.model.*;
import com.chemz.lms.repository.CourseRepository;
import com.chemz.lms.repository.EnrollmentRepository;
import com.chemz.lms.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;

    public CourseService(CourseRepository courseRepository,
                         EnrollmentRepository enrollmentRepository,
                         StudentRepository studentRepository) {
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.studentRepository = studentRepository;
    }

    // --- Create a course ---
    public Course createCourse(Course course) {
        return courseRepository.save(course);
    }

    // --- Get all courses ---
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    // --- Get course by ID ---
    public java.util.Optional<Course> getCourseById(Long id) {
        return courseRepository.findById(id);
    }

    // --- Update course (basic fields) ---
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

    // --- Count courses ---
    public long countCourses() {
        return courseRepository.count();
    }

    // --- Save course ---
    public Course saveCourse(Course course) {
        return courseRepository.save(course);
    }

    // --- Get students via repository ---
    public List<Student> getStudentsForCourse(Long courseId) {
        return enrollmentRepository.findStudentsByCourseId(courseId);
    }

    // --- Update course with students safely (prevents LazyInitializationException) ---
    @Transactional
    public Course updateCourseWithStudents(Long courseId,
                                           String courseName,
                                           String description,
                                           Teacher teacher,
                                           List<Long> studentIds) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        course.setCourseName(courseName);
        course.setDescription(description);
        course.setTeacher(teacher);

        // Initialize lazy collection
        course.getEnrollments().size();

        // Clear old enrollments
        course.getEnrollments().clear();

        // Add new enrollments
        if (studentIds != null) {
            for (Long studentId : studentIds) {
                Student student = studentRepository.findById(studentId)
                        .orElseThrow(() -> new RuntimeException("Student not found: " + studentId));
                Enrollment enrollment = new Enrollment(student, course);
                course.getEnrollments().add(enrollment);
            }
        }

        return courseRepository.save(course);
    }

    // --- NEW: Update enrollments from user IDs (for /with-users endpoint) ---
    @Transactional
    public void updateEnrollmentsFromUserIds(Course course, List<Long> studentUserIds, StudentService studentService) {
        course.getEnrollments().size(); // initialize lazy collection
        course.getEnrollments().clear();

        if (studentUserIds != null) {
            for (Long userId : studentUserIds) {
                Student student = studentService.getStudentByUser(String.valueOf(userId))
                        .orElseThrow(() -> new RuntimeException("Student not found for user: " + userId));
                Enrollment enrollment = new Enrollment(student, course);
                course.getEnrollments().add(enrollment);
            }
        }
    }
}
