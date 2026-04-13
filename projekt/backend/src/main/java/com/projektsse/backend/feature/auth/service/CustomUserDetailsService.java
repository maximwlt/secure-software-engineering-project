package com.projektsse.backend.feature.auth.service;

import com.projektsse.backend.feature.auth.model.CustomUserDetails;
import com.projektsse.backend.feature.auth.repository.UserRepository;
import com.projektsse.backend.feature.auth.repository.entities.User;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @NullMarked
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        User user  = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId));
        return new CustomUserDetails(user);
    }
}
