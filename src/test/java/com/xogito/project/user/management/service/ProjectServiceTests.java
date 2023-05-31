package com.xogito.project.user.management.service;

import com.xogito.project.user.management.dto.ProjectDTO;
import com.xogito.project.user.management.mapper.ProjectUserMapper;
import com.xogito.project.user.management.model.Project;
import com.xogito.project.user.management.model.User;
import com.xogito.project.user.management.repository.ProjectRepository;
import com.xogito.project.user.management.repository.UserRepository;
import com.xogito.project.user.management.service.impl.ProjectServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ProjectServiceTests {
    @InjectMocks
    private ProjectServiceImpl projectService;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ProjectUserMapper projectMapper;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void findProjectById_ProjectExists_ReturnsProject() {
        // Arrange
        UUID projectId = UUID.randomUUID();
        Project project = new Project();
        project.setId(projectId);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        // Act
        Project result = projectService.findProjectById(projectId);

        // Assert
        assertEquals(projectId, result.getId());
    }

    @Test
    public void findProjectById_withNonExistingProjectId_ReturnsThrowIllegalArgumentException() {
        // Arrange
        UUID projectId = UUID.randomUUID();
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(IllegalArgumentException.class, () -> projectService.findProjectById(projectId));
        verify(projectRepository, times(1)).findById(projectId);
    }

    @Test
    public void createProject_WithValidProject_ReturnsTrue() {
        // Arrange
        ProjectDTO projectDto = new ProjectDTO();
        projectDto.setName("Test Project");

        Project project = new Project();
        project.setName("Test Project");

        when(projectMapper.mapProjectDtoToProject(projectDto)).thenReturn(project);
        when(projectRepository.save(project)).thenReturn(project);

        // Act
        Project createdProject = projectService.createProject(projectDto);

        // Assert
        assertNotNull(createdProject);
        assertEquals("Test Project", createdProject.getName());
        verify(projectMapper, times(1)).mapProjectDtoToProject(projectDto);
        verify(projectRepository, times(1)).save(project);
    }

    @Test
    public void updateProject_ProjectExists_SuccessfullyUpdated() {
        // Arrange
        UUID projectId = UUID.randomUUID();
        ProjectDTO updatedProject = new ProjectDTO();
        updatedProject.setName("Updated Project");
        updatedProject.setDescription("Updated Description");

        Project existingProject = new Project();
        existingProject.setId(projectId);
        existingProject.setName("Old Project");
        existingProject.setDescription("Old Description");

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(existingProject));
        when(projectMapper.mapProjectDtoToProject(updatedProject)).thenReturn(existingProject);

        // Act
        Project result = projectService.updateProject(projectId, updatedProject);

        // Assert
        assertEquals(existingProject, result);
        assertEquals("Updated Project", updatedProject.getName());
        assertEquals("Updated Description", updatedProject.getDescription());
        verify(projectRepository, times(1)).findById(projectId);
        verify(projectRepository, times(1)).save(existingProject);
    }

    @Test
    public void deleteProject_ExistingProject_SuccessfullyDeleted() {
        // Arrange
        UUID projectId = UUID.randomUUID();
        Project project = new Project();
        project.setId(projectId);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        // Act
        ResponseEntity<Object> response = projectService.deleteProject(projectId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Project deleted successfully!", response.getBody());
        verify(projectRepository, times(1)).findById(projectId);
        verify(projectRepository, times(1)).deleteById(projectId);
    }

    @Test
    public void deleteProject_NonExistingProject_ReturnsBadRequest() {
        // Arrange
        UUID projectId = UUID.randomUUID();

        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Object> response = projectService.deleteProject(projectId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Project does not exist!", response.getBody());
        verify(projectRepository, times(1)).findById(projectId);
        verify(projectRepository, never()).deleteById(projectId);
    }

    @Test
    public void searchProjectsByName_WithValidName_ReturnsTrue() {
        // Arrange
        String name = "test";
        int pageNumber = 0;
        int pageSize = 10;

        Project project1 = new Project();
        project1.setId(UUID.randomUUID());
        project1.setName("test");
        project1.setDescription("test");

        Project project2 = new Project();
        project2.setId(UUID.randomUUID());
        project2.setName("test2");
        project2.setDescription("test2");

        // Create a sample list of projects
        List<Project> projects = new ArrayList<>();

        projects.add(project1);
        projects.add(project2);

        Page<Project> page = new PageImpl<>(projects);

        // Mock the behavior of the project repository
        when(projectRepository.searchByName(name, PageRequest.of(pageNumber, pageSize))).thenReturn(page);

        // Act
        List<Project> result = projectService.searchProjectsByName(name, pageNumber, pageSize);

        // Assert
        assertEquals(projects, result);
    }

    @Test
    public void getAllProjects_ReturnsAllProjects() {
        // Arrange
        List<Project> projects = new ArrayList<>();

        Project project1 = new Project();
        UUID projectId1 = UUID.randomUUID();
        project1.setId(projectId1);
        project1.setName("Project 1");
        project1.setDescription("Description 1");

        Project project2 = new Project();
        UUID projectId2 = UUID.randomUUID();
        project2.setId(projectId2);
        project2.setName("Project 2");
        project2.setDescription("Description 2");

        projects.add(project1);
        projects.add(project2);

        // Mock the behavior of the projectRepository
        when(projectRepository.findAll()).thenReturn(projects);

        // Act
        List<ProjectDTO> result = projectService.getAllProjects();

        // Assert
        assertEquals(2, result.size());
        assertEquals(projectId1, result.get(0).getId());
        assertEquals("Project 1", result.get(0).getName());
        assertEquals("Description 1", result.get(0).getDescription());
        assertEquals(projectId2, result.get(1).getId());
        assertEquals("Project 2", result.get(1).getName());
        assertEquals("Description 2", result.get(1).getDescription());
    }

    @Test
    public void removeUserFromProject_UserAndProjectExist_UserRemovedSuccessfully() {
        // Arrange
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Project project = new Project();
        User user = new User();

        project.setAssignedUsers(new ArrayList<>());
        user.setAssignedProjects(new ArrayList<>());

        project.getAssignedUsers().add(user);
        user.getAssignedProjects().add(project);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act
        projectService.removeUserFromProject(projectId, userId);

        // Assert
        verify(projectRepository, times(1)).findById(projectId);
        verify(userRepository, times(1)).findById(userId);
        verify(projectRepository, times(1)).save(project);
        verify(userRepository, times(1)).save(user);
        assertTrue(project.getAssignedUsers().isEmpty());
        assertTrue(user.getAssignedProjects().isEmpty());
    }
}

