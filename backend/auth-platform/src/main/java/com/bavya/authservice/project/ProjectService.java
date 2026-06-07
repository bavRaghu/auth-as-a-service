package com.bavya.authservice.project;

import com.bavya.authservice.user.User;
import com.bavya.authservice.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectMemberRepository projectMemberRepository;

    public ProjectResponse createProject(
            CreateProjectRequest request
    ) {

        String email =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getName();

        User owner =
                userRepository.findByEmail(email)
                        .orElseThrow();

        Project project = new Project();

        project.setName(request.name());
        project.setOwner(owner);

        projectRepository.save(project);

        ProjectMember membership =
                new ProjectMember();

        membership.setProject(project);
        membership.setUser(owner);
        membership.setRole(Role.OWNER);

        projectMemberRepository.save(membership);

        return new ProjectResponse(
                project.getId(),
                project.getName()
        );
    }

    public List<ProjectResponse> getProjects() {

        String email =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getName();

        User owner =
                userRepository.findByEmail(email)
                        .orElseThrow();

        return projectRepository
                .findByOwner(owner)
                .stream()
                .map(project ->
                        new ProjectResponse(
                                project.getId(),
                                project.getName()
                        ))
                .toList();
    }

    private ProjectMember getMembership(
            Long projectId,
            User user
    ) {
        return projectMemberRepository
                .findByProjectIdAndUser(
                        projectId,
                        user
                )
                .orElseThrow(
                        () -> new RuntimeException(
                                "Not a member of project"
                        )
                );
    }

    private void requireOwner(
            Long projectId,
            User user
    ) {

        ProjectMember membership =
                getMembership(
                        projectId,
                        user
                );

        if (membership.getRole()
                != Role.OWNER) {

            throw new RuntimeException(
                    "Only owners can perform this action"
            );
        }
    }

    public void addMember(
            Long projectId,
            AddMemberRequest request
    ) {
        System.out.println("ADD MEMBER ENDPOINT HIT");

        String currentEmail =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getName();

        User currentUser =
                userRepository
                        .findByEmail(currentEmail)
                        .orElseThrow();

        requireOwner(
                projectId,
                currentUser
        );

        User targetUser =
                userRepository
                        .findByEmail(request.email())
                        .orElseThrow();

        if (
                projectMemberRepository
                        .existsByProjectIdAndUser(
                                projectId,
                                targetUser
                        )
        ) {
            throw new RuntimeException(
                    "Already a member"
            );
        }

        Project project =
                projectRepository
                        .findById(projectId)
                        .orElseThrow();

        ProjectMember membership =
                new ProjectMember();

        membership.setUser(targetUser);
        membership.setProject(project);
        membership.setRole(request.role());

        projectMemberRepository
                .save(membership);
    }
}