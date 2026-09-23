package com.tecnil.my_job_api.service;

import com.tecnil.my_job_api.entity.User;
import com.tecnil.my_job_api.enums.UserRole;
import com.tecnil.my_job_api.repository.UserRepository;
import com.tecnil.my_job_api.utils.BCrypt;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService{
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

        user.setPassword(encodePassword(user.getPassword()));
        return repository.save(user);
    }

    public List<User> findAll(){
        return repository.findAll();
    }

    public Optional<User> findByUsername(String username){
        return  repository.findByUsername(username);
    }

    public  Optional<User> findById(UUID id){
        return repository.findById(id);
    }

    public Optional<User> findByEmail(String email){
        return repository.findByEmail(email);
    }

    public  Optional<User> findByPhone(String phone){
        return repository.findByPhone(phone);
    }

    public Optional<List<User>> findByRole(String role){
        return repository.findByRole(role);
    }

    public User update(@Valid User user, String id){
        User old = repository.findById(UUID.fromString(id)).get();
        old.setName(user.getName());
        old.setEmail(user.getEmail());
        old.setPhone(user.getPhone());
        old.setPassword(encodePassword(user.getPassword()));
        old.setUpdated_at(new Timestamp(System.currentTimeMillis()));
        old.setRole(user.getRole());
        old.setUsername(user.getUsername());

        return  repository.save(old);
    }

    public void delete(String id){
        if (repository.existsById(UUID.fromString(id))){
            repository.deleteById(UUID.fromString(id));
        }else throw  new IllegalArgumentException();
    }

    private String encodePassword(String pass){
        return BCrypt.encode(pass);
    }
}
