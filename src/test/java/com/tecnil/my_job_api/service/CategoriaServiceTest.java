package com.tecnil.my_job_api.service;

import com.tecnil.my_job_api.entity.Categoria;
import com.tecnil.my_job_api.exception.ResourceNotFoundException;
import com.tecnil.my_job_api.exception.UserAlreadyExistsException;
import com.tecnil.my_job_api.repository.CategoriaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoriaServiceTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private CategoriaService categoriaService;

    private Categoria sampleCategoria;
    private UUID sampleId;

    @BeforeEach
    void setUp() {
        sampleId = UUID.randomUUID();
        sampleCategoria = Categoria.builder()
                .categoriaId(sampleId)
                .nombre("Tecnología")
                .descripcion("Puestos de IT")
                .build();
    }

    @Test
    void create_WhenValid_ShouldSave() {
        when(categoriaRepository.existsByNombreIgnoreCase("Tecnología")).thenReturn(false);
        when(categoriaRepository.save(any(Categoria.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Categoria created = categoriaService.create(sampleCategoria);

        assertNotNull(created);
        assertEquals("Tecnología", created.getNombre());
        verify(categoriaRepository).save(any(Categoria.class));
    }

    @Test
    void create_WhenDuplicate_ShouldThrowException() {
        when(categoriaRepository.existsByNombreIgnoreCase("Tecnología")).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> categoriaService.create(sampleCategoria));
        verify(categoriaRepository, never()).save(any(Categoria.class));
    }

    @Test
    void findOrCreate_WhenExists_ShouldReturnExisting() {
        when(categoriaRepository.findByNombreIgnoreCase("Tecnología")).thenReturn(Optional.of(sampleCategoria));

        Categoria result = categoriaService.findOrCreate("Tecnología");

        assertEquals(sampleCategoria, result);
        verify(categoriaRepository, never()).save(any(Categoria.class));
    }

    @Test
    void findOrCreate_WhenDoesNotExist_ShouldCreateNew() {
        when(categoriaRepository.findByNombreIgnoreCase("Remoto")).thenReturn(Optional.empty());
        when(categoriaRepository.save(any(Categoria.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Categoria result = categoriaService.findOrCreate("Remoto");

        assertNotNull(result);
        assertEquals("Remoto", result.getNombre());
        verify(categoriaRepository).save(any(Categoria.class));
    }

    @Test
    void delete_WhenExists_ShouldDelete() {
        when(categoriaRepository.existsById(sampleId)).thenReturn(true);

        categoriaService.delete(sampleId);

        verify(categoriaRepository).deleteById(sampleId);
    }

    @Test
    void delete_WhenNotExists_ShouldThrowException() {
        when(categoriaRepository.existsById(sampleId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> categoriaService.delete(sampleId));
    }
}
