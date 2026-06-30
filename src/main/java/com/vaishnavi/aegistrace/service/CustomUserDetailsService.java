package com.vaishnavi.aegistrace.service;

import java.util.Collections;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.vaishnavi.aegistrace.entity.User;
import com.vaishnavi.aegistrace.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository repository;

    public CustomUserDetailsService(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User u = repository.findAll().stream().filter(x -> username.equals(x.getUsername())).findFirst().orElse(null);
        if (u == null) throw new UsernameNotFoundException("User not found");
        return org.springframework.security.core.userdetails.User.builder()
                .username(u.getUsername())
                .password(u.getPassword())
                .authorities(Collections.singletonList(new SimpleGrantedAuthority(u.getRole())))
                .build();
    }
}
