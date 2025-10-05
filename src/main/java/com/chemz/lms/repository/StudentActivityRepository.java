package com.chemz.lms.repository;

import com.chemz.lms.model.StudentActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface StudentActivityRepository extends JpaRepository<StudentActivity, Long> {

    // Find a specific StudentActivity by student + activity
    Optional<StudentActivity> findByStudentIdAndActivityId(Long studentId, Long activityId);

    // Get all activities for a specific student
    List<StudentActivity> findByStudentId(Long studentId);

    // Get all students for a specific activity
    List<StudentActivity> findByActivityId(Long activityId);

    @Query("SELECT sa.score FROM StudentActivity sa " +
            "WHERE sa.student.id = :studentId " +
            "AND sa.activity.course.id = :courseId")
    List<Integer> findScoresByStudentAndCourse(@Param("studentId") Long studentId,
                                               @Param("courseId") Long courseId);
}
