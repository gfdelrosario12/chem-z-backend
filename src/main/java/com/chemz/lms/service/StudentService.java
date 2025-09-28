package com.chemz.lms.service;

import com.chemz.lms.dto.CourseDTO;
import com.chemz.lms.dto.StudentDTO;
import com.chemz.lms.dto.TeacherDTO;
import com.chemz.lms.model.Course;
import com.chemz.lms.model.Student;
import com.chemz.lms.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    // Constructor injection
    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    // --- CRUD operations ---
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Optional<Student> getStudentById(Long id) {
        return studentRepository.findById(id);
    }

    public Student createStudent(Student student) {
        return studentRepository.save(student);
    }

    public Student updateStudent(Long id, Student updatedStudent) {
        return studentRepository.findById(id).map(student -> {
            student.setUsername(updatedStudent.getUsername());
            student.setEmail(updatedStudent.getEmail());
            student.setPassword(updatedStudent.getPassword());
            student.setFirstName(updatedStudent.getFirstName());
            student.setMiddleName(updatedStudent.getMiddleName());
            student.setLastName(updatedStudent.getLastName());
            return studentRepository.save(student);
        }).orElseThrow(() -> new RuntimeException("Student not found"));
    }

    public void deleteStudent(Long id) {
        studentRepository.deleteById(id);
    }

    // --- Student-specific actions ---
    public Optional<Student> getStudentByUsername(String username) {
        return studentRepository.findByUsername(username);
    }

    public Optional<Student> getStudentByEmail(String email) {
        return studentRepository.findByEmail(email);
    }

    public List<Long> getEnrolledCourseIds(Long studentId) {
        return studentRepository.findById(studentId)
                .map(student -> student.getEnrollments()
                        .stream()
                        .map(enrollment -> enrollment.getCourse().getId())
                        .toList())
                .orElseThrow(() -> new RuntimeException("Student not found"));
    }

    public List<Course> getEnrolledCourses(Long studentId) {
        return studentRepository.findById(studentId)
                .map(Student::getEnrolledCourses)
                .orElseThrow(() -> new RuntimeException("Student not found"));
    }

    public Optional<Student> getStudentByUser(String userIdentifier) {
        return studentRepository.findByUsername(userIdentifier)
                .or(() -> studentRepository.findByEmail(userIdentifier));
    }

    public List<StudentDTO> getAllStudentDTOs() {
        return studentRepository.findAll().stream()
                .map(s -> new StudentDTO(s.getId(), s.getFirstName(), s.getLastName()))
                .collect(Collectors.toList());
    }

    // Convert enrolled courses to DTOs (used by controller)
    public List<CourseDTO> getEnrolledCoursesDTO(Long studentId) {
        return getEnrolledCourses(studentId)
                .stream()
                .map(course -> new CourseDTO(
                        course.getId(),
                        course.getCourseName(),
                        course.getDescription(),
                        course.getTeacher() != null
                                ? new TeacherDTO(
                                course.getTeacher().getId(),
                                course.getTeacher().getFirstName(),
                                course.getTeacher().getLastName())
                                : null,
                        course.getEnrollments()
                                .stream()
                                .map(e -> new StudentDTO(
                                        e.getStudent().getId(),
                                        e.getStudent().getFirstName(),
                                        e.getStudent().getLastName()
                                ))
                                .toList()
                ))
                .toList();
    }
}
