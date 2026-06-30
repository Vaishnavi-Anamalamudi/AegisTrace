package com.vaishnavi.aegistrace.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vaishnavi.aegistrace.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsernameIgnoreCase(String username);

    boolean existsByUsernameIgnoreCase(String username);

    boolean existsByEmailIgnoreCase(String email);
}
