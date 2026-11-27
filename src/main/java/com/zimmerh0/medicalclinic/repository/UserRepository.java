package com.zimmerh0.medicalclinic.repository;

import com.zimmerh0.medicalclinic.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
}
