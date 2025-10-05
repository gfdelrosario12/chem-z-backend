package com.chemz.lms.repository;

import com.chemz.lms.model.Enrollment;
import com.chemz.lms.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    // Fetch all enrollments for a specific course
    List<Enrollment> findByCourseId(Long courseId);

    // OR directly fetch students for a course
    @Query("SELECT e.student FROM Enrollment e WHERE e.course.id = :courseId")
    List<Student> findStudentsByCourseId(@Param("courseId") Long courseId);
    List<Enrollment> findByStudentId(Long studentId);
}
