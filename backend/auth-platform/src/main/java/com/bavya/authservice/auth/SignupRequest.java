package com.bavya.authservice.auth;

public record SignupRequest(
        String email,
        String password
) {
}