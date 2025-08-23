package com.chemz.lms.controller;

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

    public CourseController(CourseService courseService, StudentService studentService, TeacherService teacherService) {
        this.courseService = courseService;
        this.studentService = studentService;
        this.teacherService = teacherService;
    }

    // --- Create a new course with a teacher ---
    @PostMapping
    public ResponseEntity<Course> createCourse(@RequestParam Long teacherId,
                                               @RequestBody Course course) {
        Teacher teacher = teacherService.getTeacherById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));
        course.setTeacher(teacher);
        return ResponseEntity.ok(courseService.createCourse(course));
    }

    @GetMapping
    public ResponseEntity<List<Course>> getAllCourses() {
        return ResponseEntity.ok(courseService.getAllCourses());
    }

    // --- Enroll a student in a course ---
    @PostMapping("/{courseId}/enroll/{studentId}")
    public ResponseEntity<Enrollment> enrollStudent(@PathVariable Long courseId,
                                                    @PathVariable Long studentId) {
        Student student = studentService.getStudentById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        Course course = courseService.getCourseById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        return ResponseEntity.ok(courseService.enrollStudent(student, course));
    }

    // --- Update student grade for enrollment ---
    @PutMapping("/enrollment/{id}/grade")
    public ResponseEntity<Enrollment> updateGrade(@PathVariable Long id,
                                                  @RequestParam Double grade) {
        return ResponseEntity.ok(courseService.updateGrade(id, grade));
    }

    // --- List students enrolled in a course ---
    @GetMapping("/{courseId}/students")
    public ResponseEntity<List<Enrollment>> getStudents(@PathVariable Long courseId) {
        return ResponseEntity.ok(courseService.getStudentsInCourse(courseId));
    }
}
