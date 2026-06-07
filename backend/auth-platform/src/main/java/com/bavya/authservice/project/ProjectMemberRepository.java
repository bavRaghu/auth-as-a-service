package com.bavya.authservice.project;

import com.bavya.authservice.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectMemberRepository
        extends JpaRepository<ProjectMember, Long> {

    List<ProjectMember> findByProjectId(Long projectId);

    Optional<ProjectMember> findByProjectIdAndUser(
            Long projectId,
            User user
    );

    boolean existsByProjectIdAndUser(
            Long projectId,
            User user
    );

    Optional<ProjectMember>
    findByProjectIdAndUserId(
            Long projectId,
            User userId
    );
}