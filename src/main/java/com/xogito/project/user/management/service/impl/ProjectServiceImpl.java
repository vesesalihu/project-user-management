package com.xogito.project.user.management.service.impl;

import com.xogito.project.user.management.dto.ProjectDTO;
import com.xogito.project.user.management.mapper.ProjectUserMapper;
import com.xogito.project.user.management.model.Project;
import com.xogito.project.user.management.model.User;
import com.xogito.project.user.management.repository.ProjectRepository;
import com.xogito.project.user.management.repository.UserRepository;
import com.xogito.project.user.management.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectUserMapper projectMapper;

    @Override
    public Project findProjectById(UUID id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Project not found with ID: " + id));
    }

    @Override
    public Project createProject(ProjectDTO projectDto) {

        if (projectDto.getName() == null || projectDto.getName().isEmpty()) {
            throw new IllegalArgumentException("Name is required");
        }
        return projectRepository.save(projectMapper.mapProjectDtoToProject(projectDto));
    }

    @Override
    public Project updateProject(UUID id, ProjectDTO updatedProject) {
        Project project =  projectMapper.mapProjectDtoToProject(updatedProject);
        Project existingProject = projectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Project not found with ID: " + id));
        if(project.getName() != null) existingProject.setName(project.getName());
        if(project.getDescription() != null) existingProject.setDescription(project.getDescription());
        projectRepository.save(existingProject);
        return existingProject;
    }

    @Override
    public ResponseEntity<Object> deleteProject(UUID id) {
        Optional<Project> projectOptional = projectRepository.findById(id);
        if (projectOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Project does not exist!");
        }
        try {
            projectRepository.deleteById(id);
            return ResponseEntity.status(HttpStatus.CREATED).body("Project deleted successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CREATED).body(e.getMessage());
        }
    }

    @Override
    public List<Project> searchProjectsByName(String name, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Project> page = projectRepository.searchByName(name, pageable);

        if (page.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return page.getContent();
    }

    @Override
    public ResponseEntity<Object> assignUserToProject(UUID projectId, UUID userId) {
        try{
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found with ID: " + projectId));

        if (project.getAssignedUsers().contains(user)) {
            throw new IllegalArgumentException("User already assigned to the project");
        }
        user.getAssignedProjects().add(project);
        project.getAssignedUsers().add(user);
        projectRepository.save(project);
        return ResponseEntity.status(HttpStatus.CREATED).body("Assign process completed succesffully");
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Error: "+ e.getMessage());
        }
    }
    @Override
    public List<ProjectDTO> getAllProjects() {
        List<Project> projects = projectRepository.findAll();
        return projects.stream()
                .map(project -> new ProjectDTO(project.getId(), project.getName(), project.getDescription()))
                .collect(Collectors.toList());
    }

    @Override
    public void removeUserFromProject(UUID projectId, UUID userId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found with ID: " + projectId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        project.getAssignedUsers().remove(user);
        user.getAssignedProjects().remove(project);

        projectRepository.save(project);
        userRepository.save(user);
    }
}
