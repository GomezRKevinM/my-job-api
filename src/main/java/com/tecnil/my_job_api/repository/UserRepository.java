package com.tecnil.my_job_api.repository;

import com.tecnil.my_job_api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByPhone(String phone);
    List<User> findByRole(String role);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);

    boolean existsByUsernameAndUserIdNot(String username, UUID userId);
    boolean existsByEmailAndUserIdNot(String email, UUID userId);
    boolean existsByPhoneAndUserIdNot(String phone, UUID userId);
}
