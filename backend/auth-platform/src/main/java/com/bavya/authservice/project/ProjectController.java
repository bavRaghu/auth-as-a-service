package com.bavya.authservice.project;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping
    public List<ProjectResponse> getProjects() {
        return projectService.getProjects();
    }

    @PostMapping
    public ProjectResponse createProject(
            @RequestBody CreateProjectRequest request
    ) {
        return projectService.createProject(request);
    }

    @PostMapping("/{projectId}/members")
    public void addMember(
            @PathVariable Long projectId,
            @RequestBody AddMemberRequest request
    ) {
        projectService.addMember(
                projectId,
                request
        );
    }
}