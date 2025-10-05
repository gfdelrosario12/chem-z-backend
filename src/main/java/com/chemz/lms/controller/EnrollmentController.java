package com.chemz.lms.controller;

import com.chemz.lms.model.Course;
import com.chemz.lms.model.Enrollment;
import com.chemz.lms.model.Teacher;
import com.chemz.lms.repository.EnrollmentRepository;
import com.chemz.lms.service.StudentActivityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {

    private final StudentActivityService studentActivityService;
    private final EnrollmentRepository enrollmentRepository;

    public EnrollmentController(StudentActivityService studentActivityService,
                                EnrollmentRepository enrollmentRepository) {
        this.studentActivityService = studentActivityService;
        this.enrollmentRepository = enrollmentRepository;
    }

    /**
     * Get average score of a student in a specific course
     */
    @GetMapping("/{studentId}/courses/{courseId}/average")
    public ResponseEntity<Double> getAverageScore(
            @PathVariable Long studentId,
            @PathVariable Long courseId
    ) {
        double average = studentActivityService.getAverageScore(studentId, courseId);
        return ResponseEntity.ok(average);
    }

    /**
     * Get all courses for a student with the average grade per course
     */
    @GetMapping("/{studentId}/averages")
    public ResponseEntity<List<Map<String, Object>>> getCoursesWithAverage(@PathVariable Long studentId) {

        List<Enrollment> enrollments = enrollmentRepository.findByStudentId(studentId);

        List<Map<String, Object>> result = enrollments.stream().map(enrollment -> {
            Course course = enrollment.getCourse();
            Long courseId = course.getId();

            Double average = studentActivityService.getAverageScore(studentId, courseId);

            Map<String, Object> map = new HashMap<>();
            map.put("id", courseId.toString());
            map.put("name", course.getCourseName());

            // Teacher's full name safely
            if (course.getTeacher() != null) {
                Teacher teacher = course.getTeacher();
                String teacherName = teacher.getFirstName() + " " + teacher.getLastName();
                map.put("teacher", teacherName);
            } else {
                map.put("teacher", null);
            }

            map.put("averageGrade", average);
            return map;
        }).toList();

        return ResponseEntity.ok(result);
    }
}
