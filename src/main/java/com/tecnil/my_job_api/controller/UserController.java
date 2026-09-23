package com.tecnil.my_job_api.controller;

import com.tecnil.my_job_api.entity.User;
import com.tecnil.my_job_api.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/user")
public class UserController {

    private final UserService userService;

    public  UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<User> create (@Valid @RequestBody User user){
        return ResponseEntity.created(URI.create("/api/v1/user")).body(userService.create(user));
    }

    @GetMapping
    public ResponseEntity<List<User>> getAll(){
        List<User> users = userService.findAll();
        if (users.isEmpty()) {
            return  ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getById(@PathVariable String id){
        User user = userService.findById(UUID.fromString(id)).orElse(null);
        if (user == null) {
            return   ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }

    @PostMapping("{id}")
    public ResponseEntity<User> update(@PathVariable String id, @Valid @RequestBody User user){
        try{
            return ResponseEntity.ok().body(userService.update(user, id));
        } catch (Exception e) {
            return   ResponseEntity.notFound().build();
        }
    }
}
