package com.xogito.project.user.management.repository;

import com.xogito.project.user.management.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.name = ?1 AND u.email = ?2")
    Page<User> searchUsersByNameAndEmail(String name, String email, Pageable pageable);

}
