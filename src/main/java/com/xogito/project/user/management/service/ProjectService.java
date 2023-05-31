package com.xogito.project.user.management.service;

import com.xogito.project.user.management.dto.ProjectDTO;
import com.xogito.project.user.management.model.Project;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

public interface ProjectService {
    Project findProjectById(UUID id);

    Project createProject(ProjectDTO project);

    Project updateProject(UUID id, ProjectDTO project);

    ResponseEntity<Object> deleteProject(UUID id);

    List<Project> searchProjectsByName(String query,int pageNumber, int pageSize);

    ResponseEntity<Object> assignUserToProject(UUID projectId, UUID userId);

    List<ProjectDTO> getAllProjects();

    void removeUserFromProject(UUID projectId, UUID userId);
}
