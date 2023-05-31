package com.xogito.project.user.management.service;

import com.xogito.project.user.management.model.User;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

public interface UserService {

    User findUserById(UUID id);

    User createUser(User user);

    User updateUser(UUID id, User user);

    ResponseEntity<Object> deleteUser(UUID id);

    List<User> searchUsersByNameAndEmail(String name, String email, int pageNumber, int pageSize);
}
