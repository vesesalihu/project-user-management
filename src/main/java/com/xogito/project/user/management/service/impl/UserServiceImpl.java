package com.xogito.project.user.management.service.impl;

import com.xogito.project.user.management.model.User;
import com.xogito.project.user.management.repository.UserRepository;
import com.xogito.project.user.management.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
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

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User findUserById(UUID id) {
        return userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + id));
    }

    @Override
    public User createUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already exists");
        }
        try {
            return userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "An error occurred during the database transaction.");
        }
    }
    @Override
    public User updateUser(UUID id, User updatedUser) {

        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "User does not exists!"));

        existingUser.setName(updatedUser.getName());
        existingUser.setEmail(updatedUser.getEmail());
        return userRepository.save(existingUser);
    }

    @Override
    public ResponseEntity<Object> deleteUser(UUID id) {


        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User does not exist!");
        }
        try {
            userRepository.deleteById(id);
            return ResponseEntity.status(HttpStatus.CREATED).body("User deleted successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CREATED).body(e.getMessage());
        }



    }

    @Override
    public List<User> searchUsersByNameAndEmail(String name, String email, int pageNumber, int pageSize) throws EntityNotFoundException {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        List<User> searchedUser = userRepository.searchUsersByNameAndEmail(name, email, pageable).getContent();
        if (searchedUser.isEmpty()) {
            throw new EntityNotFoundException("No users found with the provided search criteria.");
        }
        return searchedUser;
    }

}
