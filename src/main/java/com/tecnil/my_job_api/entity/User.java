package com.tecnil.my_job_api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Data;

import java.sql.Timestamp;
import java.util.UUID;

@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy= GenerationType.UUID)
    @Column(unique = true, nullable = false, name = "user_id")
    private UUID user_id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column( nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    @Email
    private String email;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String phone;

    @Column(nullable = false)
    private String role;

    @Column(nullable = false, updatable = false)
    private Timestamp created_at;

    @Column(nullable = false)
    private Timestamp updated_at;
}
