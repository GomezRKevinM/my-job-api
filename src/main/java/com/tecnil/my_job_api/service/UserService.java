package com.tecnil.my_job_api.service;

import com.tecnil.my_job_api.entity.User;
import com.tecnil.my_job_api.enums.UserRole;
import com.tecnil.my_job_api.exception.ResourceNotFoundException;
import com.tecnil.my_job_api.exception.UserAlreadyExistsException;
import com.tecnil.my_job_api.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UserService(final UserRepository repository, final PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User create(User user) {
        if (user == null) {
            throw new IllegalArgumentException("El usuario no puede ser nulo");
        }
        if (user.getUsername() == null || user.getUsername().isBlank()) {
            throw new IllegalArgumentException("El nombre de usuario es obligatorio");
        }
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new IllegalArgumentException("El correo electrónico es obligatorio");
        }
        if (user.getPassword() == null || user.getPassword().isBlank()) {
            throw new IllegalArgumentException("La contraseña es obligatoria");
        }
        if (user.getPhone() == null || user.getPhone().isBlank()) {
            throw new IllegalArgumentException("El teléfono es obligatorio");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }

        // Validar unicidad antes de persistir
        if (repository.existsByUsername(user.getUsername())) {
            throw new UserAlreadyExistsException(String.format("El nombre de usuario '%s' ya está registrado", user.getUsername()));
        }
        if (repository.existsByEmail(user.getEmail())) {
            throw new UserAlreadyExistsException(String.format("El correo electrónico '%s' ya está registrado", user.getEmail()));
        }
        if (repository.existsByPhone(user.getPhone())) {
            throw new UserAlreadyExistsException(String.format("El teléfono '%s' ya está registrado", user.getPhone()));
        }

        // Asignar rol: respetar si se proporcionó uno válido o usar el rol por defecto
        if (user.getRole() != null && !user.getRole().isBlank()) {
            user.setRole(UserRole.fromString(user.getRole()).name());
        } else {
            user.setRole(UserRole.current.name());
        }

        user.setUserId(null);
        user.setPassword(encodePassword(user.getPassword()));

        return repository.save(user);
    }

    @Transactional(readOnly = true)
    public List<User> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<User> findById(UUID id) {
        if (id == null) {
            return Optional.empty();
        }
        return repository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<User> findById(String id) {
        try {
            return findById(UUID.fromString(id));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    @Transactional(readOnly = true)
    public User getUserById(UUID id) {
        return findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));
    }

    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        return repository.findByUsername(username);
    }

    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return repository.findByEmail(email);
    }

    @Transactional(readOnly = true)
    public Optional<User> findByPhone(String phone) {
        return repository.findByPhone(phone);
    }

    @Transactional(readOnly = true)
    public List<User> findByRole(String role) {
        return repository.findByRole(role);
    }

    @Transactional
    public User update(UUID id, User user) {
        if (id == null) {
            throw new IllegalArgumentException("El ID del usuario no puede ser nulo");
        }
        if (user == null) {
            throw new IllegalArgumentException("Los datos de actualización no pueden ser nulos");
        }

        User old = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));

        // Actualizar nombre
        if (user.getName() != null && !user.getName().isBlank()) {
            old.setName(user.getName());
        }

        // Validar y actualizar username único
        if (user.getUsername() != null && !user.getUsername().isBlank()) {
            if (!user.getUsername().equals(old.getUsername()) && repository.existsByUsernameAndUserIdNot(user.getUsername(), id)) {
                throw new UserAlreadyExistsException(String.format("El nombre de usuario '%s' ya está en uso por otro usuario", user.getUsername()));
            }
            old.setUsername(user.getUsername());
        }

        // Validar y actualizar email único
        if (user.getEmail() != null && !user.getEmail().isBlank()) {
            if (!user.getEmail().equals(old.getEmail()) && repository.existsByEmailAndUserIdNot(user.getEmail(), id)) {
                throw new UserAlreadyExistsException(String.format("El correo electrónico '%s' ya está en uso por otro usuario", user.getEmail()));
            }
            old.setEmail(user.getEmail());
        }

        // Validar y actualizar teléfono único
        if (user.getPhone() != null && !user.getPhone().isBlank()) {
            if (!user.getPhone().equals(old.getPhone()) && repository.existsByPhoneAndUserIdNot(user.getPhone(), id)) {
                throw new UserAlreadyExistsException(String.format("El teléfono '%s' ya está en uso por otro usuario", user.getPhone()));
            }
            old.setPhone(user.getPhone());
        }

        // Contraseña: solo actualizar si se provee una nueva contraseña válida
        if (user.getPassword() != null && !user.getPassword().isBlank()) {
            old.setPassword(encodePassword(user.getPassword()));
        }

        // Rol: validar y actualizar solo si se provee
        if (user.getRole() != null && !user.getRole().isBlank()) {
            old.setRole(UserRole.fromString(user.getRole()).name());
        }

        return repository.save(old);
    }

    @Transactional
    public User update(User user, String id) {
        UUID uuid;
        try {
            uuid = UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("El formato del ID proporcionado no es un UUID válido: " + id, e);
        }
        return update(uuid, user);
    }

    @Transactional
    public void delete(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID no puede ser nulo");
        }
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("No se puede eliminar: usuario no encontrado con ID: " + id);
        }
        repository.deleteById(id);
    }

    @Transactional
    public void delete(String id) {
        UUID uuid;
        try {
            uuid = UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("El formato del ID proporcionado no es un UUID válido: " + id, e);
        }
        delete(uuid);
    }

    private String encodePassword(String pass) {
        return passwordEncoder.encode(pass);
    }
}
