package com.chemz.lms.repository;

import com.chemz.lms.model.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    Optional<Teacher> findByUsername(String username);
    Optional<Teacher> findByEmail(String email);
}
