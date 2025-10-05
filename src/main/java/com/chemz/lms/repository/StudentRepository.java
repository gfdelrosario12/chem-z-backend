package com.chemz.lms.repository;

import com.chemz.lms.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;       // <-- add this import
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findByUsername(String username);

    Optional<Student> findByEmail(String email);

    @Query("SELECT DISTINCT s FROM Student s " +
            "JOIN s.enrollments e " +
            "JOIN e.course c " +
            "WHERE c.teacher.id = :teacherId")
    List<Student> findAllByTeacherId(@Param("teacherId") Long teacherId);
}
