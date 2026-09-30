package com.tecnil.my_job_api.controller;

import com.tecnil.my_job_api.entity.User;
import com.tecnil.my_job_api.exception.ResourceNotFoundException;
import com.tecnil.my_job_api.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private User sampleUser;
    private UUID sampleId;

    @BeforeEach
    void setUp() {
        sampleId = UUID.randomUUID();
        sampleUser = User.builder()
                .userId(sampleId)
                .username("john_doe")
                .password("secret123")
                .email("john@example.com")
                .name("John Doe")
                .phone("+1234567890")
                .role("current")
                .build();
    }

    @Test
    void create_WhenValidUser_ShouldReturn201AndLocationHeader() {
        when(userService.create(any(User.class))).thenReturn(sampleUser);

        ResponseEntity<User> response = userController.create(sampleUser);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getHeaders().getLocation());
        assertEquals("/api/v1/user/" + sampleId, response.getHeaders().getLocation().getPath());
        assertEquals(sampleUser, response.getBody());
        verify(userService).create(sampleUser);
    }

    @Test
    void getAll_WithoutRole_ShouldReturnAllUsers() {
        when(userService.findAll()).thenReturn(List.of(sampleUser));

        ResponseEntity<List<User>> response = userController.getAll(null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(userService).findAll();
        verify(userService, never()).findByRole(any());
    }

    @Test
    void getAll_WithRole_ShouldReturnFilteredUsers() {
        when(userService.findByRole("admin")).thenReturn(List.of(sampleUser));

        ResponseEntity<List<User>> response = userController.getAll("admin");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(userService).findByRole("admin");
        verify(userService, never()).findAll();
    }

    @Test
    void getById_WhenFound_ShouldReturnUser() {
        when(userService.getUserById(sampleId)).thenReturn(sampleUser);

        ResponseEntity<User> response = userController.getById(sampleId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(sampleUser, response.getBody());
        verify(userService).getUserById(sampleId);
    }

    @Test
    void getByUsername_WhenFound_ShouldReturnUser() {
        when(userService.findByUsername("john_doe")).thenReturn(java.util.Optional.of(sampleUser));

        ResponseEntity<User> response = userController.getByUsername("john_doe");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(sampleUser, response.getBody());
    }

    @Test
    void getByUsername_WhenNotFound_ShouldThrowResourceNotFoundException() {
        when(userService.findByUsername("unknown")).thenReturn(java.util.Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userController.getByUsername("unknown"));
    }

    @Test
    void getByEmail_WhenFound_ShouldReturnUser() {
        when(userService.findByEmail("john@example.com")).thenReturn(java.util.Optional.of(sampleUser));

        ResponseEntity<User> response = userController.getByEmail("john@example.com");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(sampleUser, response.getBody());
    }

    @Test
    void update_UsingPutMapping_ShouldReturnUpdatedUser() {
        User updatePayload = User.builder()
                .name("John Updated")
                .build();

        User updatedUser = User.builder()
                .userId(sampleId)
                .name("John Updated")
                .build();

        when(userService.update(eq(sampleId), any(User.class))).thenReturn(updatedUser);

        ResponseEntity<User> response = userController.update(sampleId, updatePayload);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("John Updated", response.getBody().getName());
        verify(userService).update(sampleId, updatePayload);
    }

    @Test
    void delete_ShouldReturn204NoContent() {
        doNothing().when(userService).delete(sampleId);

        ResponseEntity<Void> response = userController.delete(sampleId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(userService).delete(sampleId);
    }
}
