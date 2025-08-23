package com.chemz.lms.controller;

import com.chemz.lms.model.*;
import com.chemz.lms.service.UserService;
import com.chemz.lms.service.CourseService;
import com.chemz.lms.service.TeacherService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserService userService;
    private final TeacherService teacherService;
    private final CourseService courseService;

    public AdminController(UserService userService,
                           TeacherService teacherService,
                           CourseService courseService) {
        this.userService = userService;
        this.teacherService = teacherService;
        this.courseService = courseService;
    }

    // ===== USER CRUD =====
    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/users")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        return ResponseEntity.ok(userService.createUser(user));
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        try {
            return ResponseEntity.ok(userService.updateUser(id, user));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    // ===== TEACHER CRUD =====
    @GetMapping("/teachers")
    public List<Teacher> getAllTeachers() {
        return teacherService.getAllTeachers();
    }

    @GetMapping("/teachers/{id}")
    public ResponseEntity<Teacher> getTeacher(@PathVariable Long id) {
        return teacherService.getTeacherById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/teachers")
    public ResponseEntity<Teacher> createTeacher(@RequestBody Teacher teacher) {
        return ResponseEntity.ok(teacherService.createTeacher(teacher));
    }

    @PutMapping("/teachers/{id}")
    public ResponseEntity<Teacher> updateTeacher(@PathVariable Long id, @RequestBody Teacher teacher) {
        try {
            return ResponseEntity.ok(teacherService.updateTeacher(id, teacher));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/teachers/{id}")
    public ResponseEntity<Void> deleteTeacher(@PathVariable Long id) {
        teacherService.deleteTeacher(id);
        return ResponseEntity.noContent().build();
    }

    // ===== COURSE CRUD =====
    @GetMapping("/courses")
    public List<Course> getAllCourses() {
        return courseService.getAllCourses();
    }

    @GetMapping("/courses/{id}")
    public ResponseEntity<Course> getCourse(@PathVariable Long id) {
        return courseService.getCourseById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/courses")
    public ResponseEntity<Course> createCourse(@RequestParam String courseName,
                                               @RequestParam String description,
                                               @RequestParam Long teacherId) {
        Teacher teacher = teacherService.getTeacherById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));
        Course course = new Course();
        course.setCourseName(courseName);
        course.setDescription(description);
        course.setTeacher(teacher);
        return ResponseEntity.ok(courseService.createCourse(course));
    }

    @PutMapping("/courses/{id}")
    public ResponseEntity<Course> updateCourse(@PathVariable Long id,
                                               @RequestParam String courseName,
                                               @RequestParam String description,
                                               @RequestParam Long teacherId) {
        try {
            Teacher teacher = teacherService.getTeacherById(teacherId)
                    .orElseThrow(() -> new RuntimeException("Teacher not found"));
            return ResponseEntity.ok(courseService.updateCourse(id, courseName, description, teacher));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/courses/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }
}
