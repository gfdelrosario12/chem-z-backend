package com.chemz.lms.repository;

import com.chemz.lms.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findByUsername(String username);

    Optional<Student> findByEmail(String email);
}
