package com.bavya.authservice.auth;

import com.bavya.authservice.user.User;
import com.bavya.authservice.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    public void signup(SignupRequest request) {

        if (userRepository.existsByEmail(request.email())) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();

        user.setEmail(request.email());

        // temporary
        user.setPasswordHash(request.password());

        userRepository.save(user);
    }
}