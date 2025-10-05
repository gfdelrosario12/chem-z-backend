package com.chemz.lms.service;

import com.chemz.lms.dto.CourseGradeDTO;
import com.chemz.lms.model.Student;
import com.chemz.lms.model.StudentActivity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentGradeService {
    public List<CourseGradeDTO> getStudentCourseGrades(Student student) {
        return student.getEnrollments().stream()
                .map(enrollment -> {
                    var course = enrollment.getCourse();

                    List<StudentActivity> activitiesInCourse = student.getActivities().stream()
                            .filter(sa -> sa.getActivity().getCourse().equals(course))
                            .filter(sa -> !sa.isBlocked())
                            .toList();

                    double average = activitiesInCourse.stream()
                            .mapToInt(StudentActivity::getScore)
                            .average()
                            .orElse(0.0);

                    return new CourseGradeDTO(course.getId(), course.getCourseName(), average);
                })
                .toList();
    }
}
