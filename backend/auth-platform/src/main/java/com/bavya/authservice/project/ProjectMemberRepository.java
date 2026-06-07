package com.bavya.authservice.project;

import com.bavya.authservice.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProjectMemberRepository
        extends JpaRepository<ProjectMember, Long> {

    Optional<ProjectMember> findByProjectIdAndUser(
            Long projectId,
            User user
    );

    boolean existsByProjectIdAndUser(
            Long projectId,
            User user
    );
}