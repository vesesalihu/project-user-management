package com.xogito.project.user.management.controller;

import com.xogito.project.user.management.dto.ProjectDTO;
import com.xogito.project.user.management.model.Project;
import com.xogito.project.user.management.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;

    @GetMapping("/{id}")
    public ResponseEntity<Object> getProject(@PathVariable UUID id) {
        try {
            Project project = projectService.findProjectById(id);
            return ResponseEntity.status(HttpStatus.CREATED).body(project);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        }
    }

    @PostMapping
    public ResponseEntity<Object> createProject(@RequestBody ProjectDTO project) {
        try {
            Project isProjectCreated = projectService.createProject(project);
            return ResponseEntity.status(HttpStatus.CREATED).body(isProjectCreated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateProject(@PathVariable UUID id, @Valid @RequestBody ProjectDTO project) {
        try {
            Project updatedProjectRegister = projectService.updateProject(id, project);
            return ResponseEntity.status(HttpStatus.CREATED).body(updatedProjectRegister);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        }

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteProject(@PathVariable UUID id) {
        return projectService.deleteProject(id);
    }

    @GetMapping
    public ResponseEntity<List<Project>> searchProjectsByName(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        List<Project> projects = projectService.searchProjectsByName(name, pageNumber, pageSize);
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ProjectDTO>> getAllProjects() {
        List<ProjectDTO> projects = projectService.getAllProjects();
        return ResponseEntity.ok(projects);
    }

    @PostMapping("/{projectId}/users/{userId}")
    public ResponseEntity<Object> assignUserToProject(@PathVariable UUID projectId, @PathVariable UUID userId) {
        return projectService.assignUserToProject(projectId, userId);
        //return ResponseEntity.status(HttpStatus.CREATED).body(updatedProject);
    }

    @DeleteMapping("/{projectId}/users/{userId}")
    public ResponseEntity<String> removeUserFromProject(@PathVariable UUID projectId, @PathVariable UUID userId) {
        projectService.removeUserFromProject(projectId, userId);
        return ResponseEntity.ok("User successfully removed from the project");
    }
}
