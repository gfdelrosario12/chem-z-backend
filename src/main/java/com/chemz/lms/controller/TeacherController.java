package com.chemz.lms.controller;

import com.chemz.lms.model.Course;
import com.chemz.lms.model.Teacher;
import com.chemz.lms.service.CourseService;
import com.chemz.lms.service.TeacherService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/teachers")
public class TeacherController {

    private final TeacherService teacherService;
    private final CourseService courseService;

    public TeacherController(TeacherService teacherService, CourseService courseService) {
        this.teacherService = teacherService;
        this.courseService = courseService;
    }

    // --- Teacher CRUD ---
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
    public ResponseEntity<Teacher> updateTeacher(@PathVariable Long id, @RequestBody Teacher teacher) {
        try {
            return ResponseEntity.ok(teacherService.updateTeacher(id, teacher));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // --- Teacher-specific actions ---
    @GetMapping("/{id}/courses")
    public ResponseEntity<Set<Course>> getTeacherCourses(@PathVariable Long id) {
        return teacherService.getTeacherById(id)
                .map(teacher -> ResponseEntity.ok(teacher.getCourses()))
                .orElse(ResponseEntity.notFound().build());
    }
}
