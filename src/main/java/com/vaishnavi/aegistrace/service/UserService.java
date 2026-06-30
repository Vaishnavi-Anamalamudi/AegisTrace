package com.vaishnavi.aegistrace.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.vaishnavi.aegistrace.entity.User;
import com.vaishnavi.aegistrace.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public User createUser(User user) {
        return repository.save(user);
    }

    public List<User> getAllUsers() {
        return repository.findAll();
    }
}
