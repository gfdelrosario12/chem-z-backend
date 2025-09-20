package com.chemz.lms.mapper;

import com.chemz.lms.dto.*;
import com.chemz.lms.model.Course;

import java.util.stream.Collectors;

public class CourseMapper {
    public static CourseDTO toDTO(Course course) {
        return new CourseDTO(
                course.getId(),
                course.getCourseName(),
                course.getDescription(),
                new TeacherDTO(
                        course.getTeacher().getId(),
                        course.getTeacher().getFirstName(),
                        course.getTeacher().getLastName()
                ),
                course.getEnrollments().stream()
                        .map(e -> new StudentDTO(
                                e.getStudent().getId(),
                                e.getStudent().getFirstName(),
                                e.getStudent().getLastName()
                        ))
                        .collect(Collectors.toList())
        );
    }
}
