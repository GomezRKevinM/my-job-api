package com.tecnil.my_job_api.repository;

import com.tecnil.my_job_api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    public Optional<User> findByUsername(String username);
    public Optional<User> findByPhone(String phone);
    public Optional<List<User>> findByRole(String role);
    public Optional<User> findByEmail(String email);
}
