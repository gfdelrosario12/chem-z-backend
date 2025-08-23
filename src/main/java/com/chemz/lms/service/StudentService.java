package com.chemz.lms.service;

import com.chemz.lms.model.Course;
import com.chemz.lms.model.Student;
import com.chemz.lms.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
            // add other student-specific fields here, e.g., age, gender
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

    // Returns courses the student is enrolled in
    public List<Course> getEnrolledCourses(Long studentId) {
        return studentRepository.findById(studentId)
                .map(student -> student.getEnrollments()
                        .stream()
                        .map(enrollment -> enrollment.getCourse())
                        .toList())
                .orElseThrow(() -> new RuntimeException("Student not found"));
    }
}
