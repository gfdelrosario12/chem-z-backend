package com.chemz.lms.service;

import com.chemz.lms.dto.CourseDTO;
import com.chemz.lms.dto.StudentDTO;
import com.chemz.lms.dto.TeacherDTO;
import com.chemz.lms.model.Course;
import com.chemz.lms.model.Enrollment;
import com.chemz.lms.model.Student;
import com.chemz.lms.model.Teacher;
import com.chemz.lms.repository.TeacherRepository;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TeacherService {

    private final TeacherRepository teacherRepository;
    private final CourseService courseService;
    private final StudentActivityService studentActivityService;

    public TeacherService(TeacherRepository teacherRepository,
                          CourseService courseService,
                          StudentActivityService studentActivityService) {
        this.teacherRepository = teacherRepository;
        this.courseService = courseService;
        this.studentActivityService = studentActivityService;
    }

    // =========================
    // CRUD
    // =========================

    public List<Teacher> getAllTeachers() {
        return teacherRepository.findAll();
    }

    public Optional<Teacher> getTeacherById(Long id) {
        return teacherRepository.findById(id);
    }

    public Optional<Teacher> getTeacherByUser(String username) {
        return teacherRepository.findByUsername(username);
    }

    public Teacher createTeacher(Teacher teacher) {
        return teacherRepository.save(teacher);
    }

    public Teacher updateTeacher(Long id, Teacher updatedTeacher) {
        return teacherRepository.findById(id).map(teacher -> {
            teacher.setUsername(updatedTeacher.getUsername());
            teacher.setEmail(updatedTeacher.getEmail());
            teacher.setPassword(updatedTeacher.getPassword());
            teacher.setFirstName(updatedTeacher.getFirstName());
            teacher.setMiddleName(updatedTeacher.getMiddleName());
            teacher.setLastName(updatedTeacher.getLastName());
            teacher.setSubject(updatedTeacher.getSubject());
            return teacherRepository.save(teacher);
        }).orElseThrow(() -> new RuntimeException("Teacher not found"));
    }

    public void deleteTeacher(Long id) {
        teacherRepository.deleteById(id);
    }

    // =========================
    // COUNT
    // =========================

    public long countTeachers() {
        return teacherRepository.count();
    }

    // =========================
    // BUSINESS LOGIC
    // =========================

    @Transactional
    public List<CourseDTO> getTeacherCourses(Long teacherId) {

        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        return teacher.getCourses().stream()
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
                                .map(s -> new StudentDTO(
                                        s.getId(),
                                        s.getFirstName(),
                                        s.getLastName()
                                ))
                                .toList()
                ))
                .toList();
    }

    @Transactional
    public List<Map<String, Object>> getAllGrades(Long teacherId) {

        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        List<Map<String, Object>> result = new ArrayList<>();

        for (Course course : teacher.getCourses()) {
            for (Enrollment enrollment : course.getEnrollments()) {

                Student student = enrollment.getStudent();

                double averageGrade = studentActivityService
                        .getAverageScore(student.getId(), course.getId());

                Map<String, Object> entry = new HashMap<>();
                entry.put("studentId", student.getId());
                entry.put("studentName", student.getUsername());
                entry.put("courseId", course.getId());
                entry.put("courseName", course.getCourseName());
                entry.put("grade", averageGrade);

                result.add(entry);
            }
        }

        return result;
    }

    @Transactional
    public List<Map<String, Object>> getGradesByCourse(Long courseId) {

        Course course = courseService.getCourseById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        List<Map<String, Object>> result = new ArrayList<>();

        for (Enrollment enrollment : course.getEnrollments()) {

            Student student = enrollment.getStudent();

            double averageGrade = studentActivityService
                    .getAverageScore(student.getId(), course.getId());

            Map<String, Object> entry = new HashMap<>();
            entry.put("studentId", student.getId());
            entry.put("studentName", student.getUsername());
            entry.put("courseId", course.getId());
            entry.put("courseName", course.getCourseName());
            entry.put("grade", averageGrade);

            result.add(entry);
        }

        return result;
    }

    @Transactional
    public List<Map<String, Object>> getGradesGroupedByCourse(Long teacherId) {

        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        List<Map<String, Object>> groupedResult = new ArrayList<>();

        for (Course course : teacher.getCourses()) {

            List<Map<String, Object>> students = new ArrayList<>();

            for (Enrollment enrollment : course.getEnrollments()) {

                Student student = enrollment.getStudent();

                double averageGrade = studentActivityService
                        .getAverageScore(student.getId(), course.getId());

                Map<String, Object> studentEntry = new HashMap<>();
                studentEntry.put("studentId", student.getId());
                studentEntry.put("studentName", student.getUsername());
                studentEntry.put("grade", averageGrade);

                students.add(studentEntry);
            }

            Map<String, Object> courseEntry = new HashMap<>();
            courseEntry.put("courseId", course.getId());
            courseEntry.put("courseName", course.getCourseName());
            courseEntry.put("students", students);

            groupedResult.add(courseEntry);
        }

        return groupedResult;
    }
}