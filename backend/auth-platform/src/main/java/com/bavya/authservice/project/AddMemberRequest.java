package com.bavya.authservice.project;

public record AddMemberRequest(
        String email,
        Role role
) {
}