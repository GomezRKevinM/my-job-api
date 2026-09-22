package com.tecnil.my_job_api.service;

import com.tecnil.my_job_api.entity.User;
import com.tecnil.my_job_api.enums.UserRole;
import com.tecnil.my_job_api.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository repository;

    public UserService(final UserRepository repository) {
        this.repository = repository;
    }

    public User create(@Valid User user){
        if(user == null){
            throw  new IllegalArgumentException();
        }
        if(user.getEmail().isEmpty()){ throw   new IllegalArgumentException();}
        if(user.getName().isEmpty()){ throw  new IllegalArgumentException();}
        if(user.getPassword().isEmpty()){ throw  new IllegalArgumentException();}
        if(user.getPhone().isEmpty()){ throw  new IllegalArgumentException();}

        user.setUser_id(UUID.randomUUID());
        user.setRole(UserRole.current.name());
        user.setCreated_at(new Timestamp(System.currentTimeMillis()));
        user.setUpdated_at(new Timestamp(System.currentTimeMillis()));

        return repository.save(user);
    }
}
