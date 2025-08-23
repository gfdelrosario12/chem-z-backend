package com.chemz.lms.controller;

import com.chemz.lms.model.Course;
import com.chemz.lms.model.Student;
import com.chemz.lms.service.StudentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    // --- Student CRUD ---
    @GetMapping
    public List<Student> getAllStudents() {
        return studentService.getAllStudents();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudent(@PathVariable Long id) {
        return studentService.getStudentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Student> updateStudent(@PathVariable Long id, @RequestBody Student student) {
        try {
            return ResponseEntity.ok(studentService.updateStudent(id, student));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // --- Student-specific actions ---
    @GetMapping("/{id}/courses")
    public ResponseEntity<List<Course>> getStudentCourses(@PathVariable Long id) {
        return studentService.getStudentById(id)
                .map(student -> ResponseEntity.ok(student.getEnrolledCourses()))
                .orElse(ResponseEntity.notFound().build());
    }
}
