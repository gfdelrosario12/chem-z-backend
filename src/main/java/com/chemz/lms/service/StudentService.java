package com.chemz.lms.service;

import com.chemz.lms.dto.CourseDTO;
import com.chemz.lms.dto.StudentDTO;
import com.chemz.lms.dto.TeacherDTO;
import com.chemz.lms.model.Course;
import com.chemz.lms.model.Student;
import com.chemz.lms.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

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

    public Optional<Student> getStudentByUser(String userIdentifier) {
        return studentRepository.findByUsername(userIdentifier)
                .or(() -> studentRepository.findByEmail(userIdentifier));
    }

    public List<StudentDTO> getAllStudentDTOs() {
        return studentRepository.findAll().stream()
                .map(s -> new StudentDTO(
                        s.getId(),
                        s.getFirstName(),
                        s.getLastName()
                ))
                .collect(Collectors.toList());
    }

    // =========================================================
    // FIXED METHODS (Transactional to prevent LazyInitialization)
    // =========================================================

    @Transactional
    public List<Long> getEnrolledCourseIds(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        return student.getEnrollments()
                .stream()
                .map(enrollment -> enrollment.getCourse().getId())
                .toList();
    }

    @Transactional
    public List<Course> getEnrolledCourses(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        return student.getEnrollments()
                .stream()
                .map(enrollment -> enrollment.getCourse())
                .toList();
    }

    @Transactional
    public List<CourseDTO> getEnrolledCoursesDTO(Long studentId) {

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        return student.getEnrollments()
                .stream()
                .map(enrollment -> {
                    Course course = enrollment.getCourse();

                    return new CourseDTO(
                            course.getId(),
                            course.getCourseName(),
                            course.getDescription(),
                            course.getTeacher() != null
                                    ? new TeacherDTO(
                                    course.getTeacher().getId(),
                                    course.getTeacher().getFirstName(),
                                    course.getTeacher().getLastName()
                            )
                                    : null,
                            null
                    );
                })
                .toList();
    }
}