package com.bavya.authservice.apikey;

import com.bavya.authservice.project.Project;
import com.bavya.authservice.ratelimit.RateLimitService;
import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class ApiKeyAuthenticationFilter
        extends OncePerRequestFilter {

    private final ApiKeyService apiKeyService;

    public ApiKeyAuthenticationFilter(
            ApiKeyService apiKeyService, RateLimitService rateLimitService
    ) {
        this.apiKeyService = apiKeyService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            @Nonnull HttpServletResponse response,
            @Nonnull FilterChain filterChain
    )
            throws ServletException, IOException {

        String apiKey =
                request.getHeader(
                        "X-API-Key"
                );

        if (apiKey != null) {

            try {

                Project project =
                        apiKeyService
                                .validateApiKey(
                                        apiKey
                                );

                request.setAttribute(
                        "project",
                        project
                );

            } catch (Exception ignored) {
            }
        }

        filterChain.doFilter(
                request,
                response
        );
    }
}