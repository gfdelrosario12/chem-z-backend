package com.chemz.lms.controller;

import com.chemz.lms.dto.*;
import com.chemz.lms.mapper.CourseMapper;
import com.chemz.lms.model.*;
import com.chemz.lms.service.CourseService;
import com.chemz.lms.service.StudentService;
import com.chemz.lms.service.TeacherService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;
    private final StudentService studentService;
    private final TeacherService teacherService;

    public CourseController(CourseService courseService,
                            StudentService studentService,
                            TeacherService teacherService) {
        this.courseService = courseService;
        this.studentService = studentService;
        this.teacherService = teacherService;
    }

    // --- Create a new course with a teacher ---
    @PostMapping
    public ResponseEntity<CourseDTO> createCourse(@RequestParam Long teacherId,
                                                  @RequestBody Course course) {
        Teacher teacher = teacherService.getTeacherById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));
        course.setTeacher(teacher);
        Course saved = courseService.createCourse(course);
        return ResponseEntity.ok(CourseMapper.toDTO(saved));
    }

    // --- Get all courses ---
    @GetMapping
    public ResponseEntity<List<CourseDTO>> getAllCourses() {
        return ResponseEntity.ok(
                courseService.getAllCourses().stream()
                        .map(CourseMapper::toDTO)
                        .toList()
        );
    }

    // --- Get course by ID ---
    @GetMapping("/{id}")
    public ResponseEntity<CourseDTO> getCourseById(@PathVariable Long id) {
        Course course = courseService.getCourseById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        return ResponseEntity.ok(CourseMapper.toDTO(course));
    }

    // --- Enroll a student in a course ---
    @PostMapping("/{courseId}/enroll/{studentId}")
    public ResponseEntity<CourseDTO> enrollStudent(@PathVariable Long courseId,
                                                   @PathVariable Long studentId) {
        Student student = studentService.getStudentById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        Course course = courseService.getCourseById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        courseService.enrollStudent(student, course);
        return ResponseEntity.ok(CourseMapper.toDTO(course));
    }

    // --- Update student grade for enrollment ---
    @PutMapping("/enrollment/{id}/grade")
    public ResponseEntity<EnrollmentDTO> updateGrade(@PathVariable Long id,
                                                     @RequestParam Double grade) {
        Enrollment enrollment = courseService.updateGrade(id, grade);
        return ResponseEntity.ok(new EnrollmentDTO(
                enrollment.getId(),
                enrollment.getStudent().getId(),
                enrollment.getStudent().getFirstName(),
                enrollment.getStudent().getLastName(),
                enrollment.getGrade()
        ));
    }

    // --- List students enrolled in a course ---
    @GetMapping("/{courseId}/students")
    public ResponseEntity<List<StudentDTO>> getStudents(@PathVariable Long courseId) {
        return ResponseEntity.ok(
                courseService.getStudentsInCourse(courseId).stream()
                        .map(s -> new StudentDTO(s.getId(), s.getFirstName(), s.getLastName()))
                        .toList()
        );
    }

    // --- Update a course (name, desc, teacher, students) ---
    @PutMapping("/{id}")
    public ResponseEntity<CourseDTO> updateCourse(
            @PathVariable Long id,
            @RequestBody CourseUpdateDTO dto) {

        Course course = courseService.getCourseById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        Teacher teacher = teacherService.getTeacherById(dto.getTeacherId())
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        // Update core fields
        course.setCourseName(dto.getCourseName());
        course.setDescription(dto.getDescription());
        course.setTeacher(teacher);

        // Update enrollments
        if (dto.getStudentIds() != null) {
            course.getEnrollments().clear();
            for (Long studentId : dto.getStudentIds()) {
                Student student = studentService.getStudentById(studentId)
                        .orElseThrow(() -> new RuntimeException("Student not found: " + studentId));
                Enrollment enrollment = new Enrollment(student, course);
                course.getEnrollments().add(enrollment);
            }
        }

        Course updated = courseService.createCourse(course);
        return ResponseEntity.ok(CourseMapper.toDTO(updated));
    }

    // --- Delete a course ---
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }
}
