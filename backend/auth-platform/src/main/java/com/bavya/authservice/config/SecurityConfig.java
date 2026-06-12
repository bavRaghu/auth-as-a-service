package com.bavya.authservice.config;

import com.bavya.authservice.apikey.ApiKeyAuthenticationFilter;
import com.bavya.authservice.auth.OAuth2SuccessHandler;
import com.bavya.authservice.jwt.JwtAuthenticationFilter;
import com.bavya.authservice.ratelimit.RateLimitFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final ApiKeyAuthenticationFilter apiKeyAuthenticationFilter;
    private final RateLimitFilter rateLimitFilter;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, ApiKeyAuthenticationFilter apiKeyAuthenticationFilter, RateLimitFilter rateLimitFilter, OAuth2SuccessHandler oAuth2SuccessHandler) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.apiKeyAuthenticationFilter = apiKeyAuthenticationFilter;
        this.rateLimitFilter = rateLimitFilter;
        this.oAuth2SuccessHandler = oAuth2SuccessHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/auth/signup",
                                "/api/auth/login",
                                "/api/auth/refresh",
                                "/api/auth/logout",
                                "/api/health",
                                "/api/client/**",
                                "/oauth2/**",
                                "/login/**"
                        )
                        .permitAll()
                        .anyRequest()
                        .authenticated()
                ).oauth2Login(oauth ->
                        oauth.successHandler(
                                oAuth2SuccessHandler
                        )
                );

        http.addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class
        );

        http.addFilterBefore(
                apiKeyAuthenticationFilter,
                JwtAuthenticationFilter.class
        );

        http.addFilterBefore(
                rateLimitFilter,
                ApiKeyAuthenticationFilter.class
        );

        return http.build();
    }
}