package com.xogito.project.user.management.service;

import com.xogito.project.user.management.model.User;
import com.xogito.project.user.management.repository.UserRepository;
import com.xogito.project.user.management.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.*;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTests {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void findUserById_UserExists_ReturnsUser() {
        // Arrange
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        user.setName("user");
        user.setEmail("user@gmail.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act
        User result = userService.findUserById(userId);

        // Assert
        verify(userRepository, times(1)).findById(userId);
        assertNotNull(result);
        assertEquals(user, result);
    }

    @Test
    public void findUserById_UserDoesNotExist_ReturnsThrowIllegalArgumentException() {
        // Arrange
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(IllegalArgumentException.class, () -> userService.findUserById(userId));
        verify(userRepository, times(1)).findById(userId);

    }

    @Test
    public void createUser_EmailDoesNotExist_UserCreatedSuccessfully() {
        // Arrange
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setName("User1");
        user.setEmail("user1@example.com");

        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
        when(userRepository.save(user)).thenReturn(user);

        // Act
        User result = userService.createUser(user);

        // Assert
        verify(userRepository, times(1)).existsByEmail(user.getEmail());
        verify(userRepository, times(1)).save(user);
        assertEquals(user, result);
    }

    @Test
    public void createUser_NewUser_SuccessfullyCreated() {
        // Arrange
        User user = new User();
        user.setEmail("test@example.com");

        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
        when(userRepository.save(user)).thenReturn(user);

        // Act
        User createdUser = userService.createUser(user);

        // Assert
        assertNotNull(createdUser);
        assertEquals(user.getEmail(), createdUser.getEmail());
        verify(userRepository, times(1)).existsByEmail(user.getEmail());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void createUser_EmailAlreadyExists_ThrowsBadRequestException() {
        // Arrange
        User user = new User();
        user.setEmail("test@example.com");

        when(userRepository.existsByEmail(user.getEmail())).thenReturn(true);

        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> userService.createUser(user));
        verify(userRepository, times(1)).existsByEmail(user.getEmail());
        verify(userRepository, never()).save(user);
    }

    @Test
    public void createUser_DatabaseError_ThrowsBadGatewayException() {
        // Arrange
        User user = new User();
        user.setEmail("test@example.com");

        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
        when(userRepository.save(user)).thenThrow(DataIntegrityViolationException.class);

        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> userService.createUser(user));
        verify(userRepository, times(1)).existsByEmail(user.getEmail());
        verify(userRepository, times(1)).save(user);
    }


    @Test
    public void updateUser_WithValidIdAndUser_ReturnsUpdatedUser() {
        // Arrange
        UUID userId = UUID.randomUUID();

        // Create the existing user
        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setName("Old User");
        existingUser.setEmail("olduser@gmail.com");

        // Create the updated user
        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setName("New User");
        updatedUser.setEmail("newuser@gmail.com");

        // Mock the userRepository
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(existingUser)).thenReturn(updatedUser);

        // Act
        User result = userService.updateUser(userId, updatedUser);

        // Assert
        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals(updatedUser.getName(), result.getName());
        assertEquals(updatedUser.getEmail(), result.getEmail());
        // Verify that the repository methods were called correctly
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(existingUser);
    }

    @Test
    public void deleteUser_UserExists_UserDeletedSuccessfully() {
        // Arrange
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act
        ResponseEntity<Object> response = userService.deleteUser(userId);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("User deleted successfully!", response.getBody());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    public void deleteUser_UserDoesNotExist_ReturnsBadRequest() {
        // Arrange
        UUID userId = UUID.randomUUID();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Object> response = userService.deleteUser(userId);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("User does not exist!", response.getBody());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).deleteById(userId);
    }

    @Test
    public void searchUsersByNameAndEmail_UsersFound_ReturnsListOfUsers() {
        // Arrange
        String name = "UserName";
        String email = "user@example.com";
        int pageNumber = 0;
        int pageSize = 10;
        List<User> users = new ArrayList<>();

        // Create User instances
        User user1 = new User();
        user1.setId(UUID.randomUUID());
        user1.setName("Vesa");
        user1.setEmail("vesa@gmail.com");

        users.add(user1);

        Page<User> userPage = new PageImpl<>(users);

        when(userRepository.searchUsersByNameAndEmail(name, email, PageRequest.of(pageNumber, pageSize)))
                .thenReturn(userPage);

        // Act
        List<User> searchedUsers = userService.searchUsersByNameAndEmail(name, email, pageNumber, pageSize);

        // Assert
        verify(userRepository, times(1))
                .searchUsersByNameAndEmail(name, email, PageRequest.of(pageNumber, pageSize));
        assertEquals(users, searchedUsers);
    }
}
