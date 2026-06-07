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
}