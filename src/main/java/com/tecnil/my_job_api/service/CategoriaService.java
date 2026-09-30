package com.tecnil.my_job_api.service;

import com.tecnil.my_job_api.entity.Categoria;
import com.tecnil.my_job_api.exception.ResourceNotFoundException;
import com.tecnil.my_job_api.exception.UserAlreadyExistsException;
import com.tecnil.my_job_api.repository.CategoriaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    @Transactional
    public Categoria create(Categoria categoria) {
        if (categoria == null || categoria.getNombre() == null || categoria.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre de la categoría es obligatorio");
        }

        String nombreTrimmed = categoria.getNombre().trim();
        if (categoriaRepository.existsByNombreIgnoreCase(nombreTrimmed)) {
            throw new UserAlreadyExistsException(String.format("La categoría '%s' ya existe", nombreTrimmed));
        }

        categoria.setCategoriaId(null);
        categoria.setNombre(nombreTrimmed);
        return categoriaRepository.save(categoria);
    }

    @Transactional(readOnly = true)
    public List<Categoria> findAll() {
        return categoriaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Categoria> findById(UUID id) {
        if (id == null) {
            return Optional.empty();
        }
        return categoriaRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Categoria getCategoriaById(UUID id) {
        return findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con ID: " + id));
    }

    @Transactional(readOnly = true)
    public Optional<Categoria> findByNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            return Optional.empty();
        }
        return categoriaRepository.findByNombreIgnoreCase(nombre.trim());
    }

    @Transactional
    public Categoria findOrCreate(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre de la categoría no puede estar vacío");
        }
        String nombreTrimmed = nombre.trim();
        return categoriaRepository.findByNombreIgnoreCase(nombreTrimmed)
                .orElseGet(() -> categoriaRepository.save(
                        Categoria.builder()
                                .nombre(nombreTrimmed)
                                .build()
                ));
    }

    @Transactional
    public void delete(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID no puede ser nulo");
        }
        if (!categoriaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Categoría no encontrada con ID: " + id);
        }
        categoriaRepository.deleteById(id);
    }
}
