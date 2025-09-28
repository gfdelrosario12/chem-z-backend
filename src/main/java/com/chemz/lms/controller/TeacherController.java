package com.chemz.lms.controller;

import com.chemz.lms.dto.CourseDTO;
import com.chemz.lms.dto.StudentDTO;
import com.chemz.lms.dto.TeacherDTO;
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
    public ResponseEntity<List<CourseDTO>> getTeacherCourses(@PathVariable Long id) {
        return teacherService.getTeacherById(id)
                .map(teacher -> teacher.getCourses().stream()
                        .map(course -> new CourseDTO(
                                course.getId(),
                                course.getCourseName(),
                                course.getDescription(),
                                new TeacherDTO(
                                        teacher.getId(),
                                        teacher.getFirstName(),
                                        teacher.getLastName()
                                ),
                                courseService.getStudentsForCourse(course.getId()).stream()
                                        .map(s -> new StudentDTO(s.getId(), s.getFirstName(), s.getLastName()))
                                        .toList()
                        ))
                        .toList())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
