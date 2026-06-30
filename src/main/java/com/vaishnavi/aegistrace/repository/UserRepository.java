package com.vaishnavi.aegistrace.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vaishnavi.aegistrace.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}
