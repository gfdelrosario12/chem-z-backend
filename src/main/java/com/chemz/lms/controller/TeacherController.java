package com.chemz.lms.controller;

import com.chemz.lms.dto.CourseDTO;
import com.chemz.lms.dto.CourseGradeDTO;
import com.chemz.lms.dto.StudentDTO;
import com.chemz.lms.dto.TeacherDTO;
import com.chemz.lms.model.Student;
import com.chemz.lms.model.Teacher;
import com.chemz.lms.repository.StudentRepository;
import com.chemz.lms.service.CourseService;
import com.chemz.lms.service.StudentGradeService;
import com.chemz.lms.service.TeacherService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.*;

@RestController
@RequestMapping("/api/teachers")
public class TeacherController {

    private final TeacherService teacherService;
    private final CourseService courseService;
    private final StudentRepository studentRepository;
    private final StudentGradeService gradeService;


    public TeacherController(TeacherService teacherService, CourseService courseService, StudentRepository studentRepository, StudentGradeService gradeService) {
        this.teacherService = teacherService;
        this.courseService = courseService;
        this.studentRepository = studentRepository;
        this.gradeService = gradeService;
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

    @GetMapping("/{teacherId}/grades")
    public ResponseEntity<List<Map<String, Object>>> getAllGrades(@PathVariable Long teacherId) {
        List<Student> students = studentRepository.findAllByTeacherId(teacherId);
        List<Map<String, Object>> result = new ArrayList<>();

        for (Student student : students) {
            List<CourseGradeDTO> grades = gradeService.getStudentCourseGrades(student);
            for (CourseGradeDTO grade : grades) {
                Map<String, Object> entry = new HashMap<>();
                entry.put("studentId", student.getId());
                entry.put("studentName", student.getUsername());
                entry.put("courseId", grade.getCourseId());
                entry.put("courseName", grade.getCourseName());
                entry.put("grade", grade.getAverageGrade());
                result.add(entry);
            }
        }

        return ResponseEntity.ok(result);
    }

}
