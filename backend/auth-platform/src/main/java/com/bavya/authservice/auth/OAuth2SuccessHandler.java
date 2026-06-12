package com.bavya.authservice.auth;

import com.bavya.authservice.jwt.JwtService;
import com.bavya.authservice.user.User;
import com.bavya.authservice.user.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler
        implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {

        OAuth2User oauthUser =
                (OAuth2User)
                        authentication.getPrincipal();

        String email =
                oauthUser.getAttribute(
                        "email"
                );

        User user =
                userRepository
                        .findByEmail(email)
                        .orElseGet(() -> {

                            User newUser =
                                    new User();

                            newUser.setEmail(email);

                            newUser.setVerified(true);

                            return userRepository
                                    .save(newUser);
                        });

        String jwt =
                jwtService.generateToken(
                        user.getEmail()
                );

        response.setContentType(
                "application/json"
        );

        response.getWriter().write(
                """
                {
                    "token":"%s"
                }
                """.formatted(jwt)
        );
    }
}