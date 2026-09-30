package com.tecnil.my_job_api.service;

import com.tecnil.my_job_api.entity.User;
import com.tecnil.my_job_api.enums.UserRole;
import com.tecnil.my_job_api.exception.ResourceNotFoundException;
import com.tecnil.my_job_api.exception.UserAlreadyExistsException;
import com.tecnil.my_job_api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User sampleUser;
    private UUID sampleId;

    @BeforeEach
    void setUp() {
        sampleId = UUID.randomUUID();
        sampleUser = User.builder()
                .userId(sampleId)
                .username("john_doe")
                .password("plainPassword123")
                .email("john@example.com")
                .name("John Doe")
                .phone("+1234567890")
                .role("current")
                .build();
    }

    @Test
    void create_WhenValidUser_ShouldEncodePasswordAndSave() {
        when(userRepository.existsByUsername("john_doe")).thenReturn(false);
        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(userRepository.existsByPhone("+1234567890")).thenReturn(false);
        when(passwordEncoder.encode("plainPassword123")).thenReturn("encodedPasswordHash");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User created = userService.create(sampleUser);

        assertNotNull(created);
        assertEquals("encodedPasswordHash", created.getPassword());
        assertEquals("current", created.getRole());
        assertNull(created.getUserId());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void create_WhenUsernameAlreadyExists_ShouldThrowException() {
        when(userRepository.existsByUsername("john_doe")).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> userService.create(sampleUser));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void create_WhenEmailAlreadyExists_ShouldThrowException() {
        when(userRepository.existsByUsername("john_doe")).thenReturn(false);
        when(userRepository.existsByEmail("john@example.com")).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> userService.create(sampleUser));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void create_WhenPhoneAlreadyExists_ShouldThrowException() {
        when(userRepository.existsByUsername("john_doe")).thenReturn(false);
        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(userRepository.existsByPhone("+1234567890")).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> userService.create(sampleUser));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void create_WhenMissingRequiredFields_ShouldThrowIllegalArgumentException() {
        sampleUser.setUsername(null);

        assertThrows(IllegalArgumentException.class, () -> userService.create(sampleUser));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void findById_WhenExists_ShouldReturnUser() {
        when(userRepository.findById(sampleId)).thenReturn(Optional.of(sampleUser));

        Optional<User> found = userService.findById(sampleId);

        assertTrue(found.isPresent());
        assertEquals("john_doe", found.get().getUsername());
    }

    @Test
    void getUserById_WhenNotFound_ShouldThrowResourceNotFoundException() {
        when(userRepository.findById(sampleId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(sampleId));
    }

    @Test
    void update_WhenValidUpdateWithoutPassword_ShouldKeepExistingPassword() {
        User existingUser = User.builder()
                .userId(sampleId)
                .username("john_doe")
                .password("existingHash")
                .email("john@example.com")
                .name("John Doe")
                .phone("+1234567890")
                .role("current")
                .build();

        User updateData = User.builder()
                .name("John Updated")
                .password(null)
                .build();

        when(userRepository.findById(sampleId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.update(sampleId, updateData);

        assertEquals("John Updated", result.getName());
        assertEquals("existingHash", result.getPassword());
        verify(passwordEncoder, never()).encode(any());
        verify(userRepository).save(existingUser);
    }

    @Test
    void update_WhenUsernameAlreadyTakenByAnother_ShouldThrowException() {
        User existingUser = User.builder()
                .userId(sampleId)
                .username("john_doe")
                .password("hash")
                .email("john@example.com")
                .name("John Doe")
                .phone("+1234567890")
                .role("current")
                .build();

        User updateData = User.builder()
                .username("taken_username")
                .build();

        when(userRepository.findById(sampleId)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByUsernameAndUserIdNot("taken_username", sampleId)).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> userService.update(sampleId, updateData));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void delete_WhenExists_ShouldDelete() {
        when(userRepository.existsById(sampleId)).thenReturn(true);

        userService.delete(sampleId);

        verify(userRepository).deleteById(sampleId);
    }

    @Test
    void delete_WhenNotExists_ShouldThrowResourceNotFoundException() {
        when(userRepository.existsById(sampleId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> userService.delete(sampleId));
        verify(userRepository, never()).deleteById(any());
    }

    @Test
    void findByRole_ShouldReturnList() {
        when(userRepository.findByRole("admin")).thenReturn(List.of(sampleUser));

        List<User> list = userService.findByRole("admin");

        assertEquals(1, list.size());
        verify(userRepository).findByRole("admin");
    }
}
