package com.chemz.lms.controller;

import com.chemz.lms.dto.CourseDTO;
import com.chemz.lms.model.Teacher;
import com.chemz.lms.service.TeacherService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teachers")
public class TeacherController {

    private final TeacherService teacherService;

    public TeacherController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    // =========================
    // Teacher CRUD
    // =========================

    @GetMapping
    public List<Teacher> getAllTeachers() {
        return teacherService.getAllTeachers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Teacher> getTeacher(@PathVariable Long id) {
        return teacherService.getTeacherById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Teacher> updateTeacher(@PathVariable Long id,
                                                 @RequestBody Teacher teacher) {
        try {
            return ResponseEntity.ok(teacherService.updateTeacher(id, teacher));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // =========================
    // Teacher Courses
    // =========================

    @GetMapping("/{id}/courses")
    public ResponseEntity<List<CourseDTO>> getTeacherCourses(@PathVariable Long id) {
        return ResponseEntity.ok(teacherService.getTeacherCourses(id));
    }

    // =========================
    // Teacher Grades
    // =========================

    @GetMapping("/{teacherId}/grades")
    public ResponseEntity<List<?>> getAllGrades(@PathVariable Long teacherId) {
        return ResponseEntity.ok(teacherService.getAllGrades(teacherId));
    }
}