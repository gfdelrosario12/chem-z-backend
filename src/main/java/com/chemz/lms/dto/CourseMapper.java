package com.chemz.lms.dto;

import com.chemz.lms.model.Course;
import com.chemz.lms.model.Enrollment;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CourseMapper {

    public static CourseDTO toDTO(Course course) {
        // Safely map enrollments
        List<StudentDTO> students;
        try {
            students = course.getEnrollments().stream()
                    .map(Enrollment::getStudent)
                    .map(s -> new StudentDTO(s.getId(), s.getFirstName(), s.getLastName()))
                    .collect(Collectors.toList());
        } catch (org.hibernate.LazyInitializationException e) {
            // Session is closed, fallback to empty list
            students = Collections.emptyList();
        }

        return new CourseDTO(
                course.getId(),
                course.getCourseName(),
                course.getDescription(),
                new TeacherDTO(
                        course.getTeacher().getId(),
                        course.getTeacher().getFirstName(),
                        course.getTeacher().getLastName()
                ),
                students
        );
    }
}
