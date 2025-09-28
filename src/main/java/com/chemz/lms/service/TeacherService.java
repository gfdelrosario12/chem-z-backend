package com.chemz.lms.service;

import com.chemz.lms.model.Teacher;
import com.chemz.lms.repository.TeacherRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TeacherService {

    private final TeacherRepository teacherRepository;

    public TeacherService(TeacherRepository teacherRepository) {
        this.teacherRepository = teacherRepository;
    }

    public List<Teacher> getAllTeachers() {
        return teacherRepository.findAll();
    }

    public Optional<Teacher> getTeacherById(Long id) {
        return teacherRepository.findById(id);
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
            teacher.setSubject(updatedTeacher.getSubject()); // use subject instead of department
            return teacherRepository.save(teacher);
        }).orElseThrow(() -> new RuntimeException("Teacher not found"));
    }

    public void deleteTeacher(Long id) {
        teacherRepository.deleteById(id);
    }

    public Optional<Teacher> getTeacherByUsername(String username) {
        return teacherRepository.findByUsername(username);
    }

    public Optional<Teacher> getTeacherByEmail(String email) {
        return teacherRepository.findByEmail(email);
    }

    public long countTeachers() {
        return teacherRepository.count();
    }

    public Optional<Teacher> getTeacherByUser(String userIdentifier) {
        // Try finding by username first
        Optional<Teacher> teacher = teacherRepository.findByUsername(userIdentifier);
        if (teacher.isPresent()) {
            return teacher;
        }
        // Fallback to email
        return teacherRepository.findByEmail(userIdentifier);
    }

}
