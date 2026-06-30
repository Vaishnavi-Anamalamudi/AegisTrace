package com.vaishnavi.aegistrace.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;

import com.vaishnavi.aegistrace.entity.User;
import com.vaishnavi.aegistrace.repository.UserRepository;

class CustomUserDetailsServiceTest {

    @Test
    void inactiveAccountIsDisabledAndRetainsItsAuthority() {
        UserRepository repository = mock(UserRepository.class);
        User user = new User();
        user.setUsername("viewer");
        user.setPassword("bcrypt-hash");
        user.setRole("ROLE_VIEWER");
        user.setStatus("SUSPENDED");
        when(repository.findByUsernameIgnoreCase("viewer")).thenReturn(Optional.of(user));

        UserDetails details = new CustomUserDetailsService(repository).loadUserByUsername("viewer");

        assertThat(details.isEnabled()).isFalse();
        assertThat(details.getAuthorities())
                .extracting("authority")
                .containsExactly("ROLE_VIEWER");
    }
}
