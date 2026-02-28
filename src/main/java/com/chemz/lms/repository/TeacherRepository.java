package com.chemz.lms.repository;

import com.chemz.lms.model.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.List;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {

    // Find by username
    Optional<Teacher> findByUsername(String username);

    // Find by email
    Optional<Teacher> findByEmail(String email);

    // Fetch teacher with courses eagerly to prevent LazyInitializationException
    @Query("SELECT t FROM Teacher t LEFT JOIN FETCH t.courses WHERE t.id = :id")
    Optional<Teacher> findByIdWithCourses(@Param("id") Long id);

    // Fetch all teachers with courses (optional, for listing)
    @Query("SELECT DISTINCT t FROM Teacher t LEFT JOIN FETCH t.courses")
    List<Teacher> findAllWithCourses();
}
