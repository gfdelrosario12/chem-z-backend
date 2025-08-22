package com.chemz.lms.controller;

import com.chemz.lms.model.*;
import com.chemz.lms.service.CourseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;
    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @PostMapping
    public ResponseEntity<Course> createCourse(@RequestBody Course course) {
        return ResponseEntity.ok(courseService.createCourse(course));
    }

    @GetMapping
    public ResponseEntity<List<Course>> getAllCourses() {
        return ResponseEntity.ok(courseService.getAllCourses());
    }

    @PostMapping("/{courseId}/enroll/{studentId}")
    public ResponseEntity<Enrollment> enrollStudent(@PathVariable Long courseId, @PathVariable Long studentId) {
        Student student = new Student(); student.setId(studentId); // proxy student
        Course course = new Course(); course.setId(courseId); // proxy course
        return ResponseEntity.ok(courseService.enrollStudent(student, course));
    }

    @PutMapping("/enrollment/{id}/grade")
    public ResponseEntity<Enrollment> updateGrade(@PathVariable Long id, @RequestParam Double grade) {
        return ResponseEntity.ok(courseService.updateGrade(id, grade));
    }

    @GetMapping("/{courseId}/students")
    public ResponseEntity<List<Enrollment>> getStudents(@PathVariable Long courseId) {
        return ResponseEntity.ok(courseService.getStudentsInCourse(courseId));
    }
}
