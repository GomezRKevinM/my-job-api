package com.tecnil.my_job_api.service;

import com.tecnil.my_job_api.dto.EmpleoRequestDTO;
import com.tecnil.my_job_api.entity.Categoria;
import com.tecnil.my_job_api.entity.Empleo;
import com.tecnil.my_job_api.entity.User;
import com.tecnil.my_job_api.exception.ResourceNotFoundException;
import com.tecnil.my_job_api.repository.CategoriaRepository;
import com.tecnil.my_job_api.repository.EmpleoRepository;
import com.tecnil.my_job_api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmpleoServiceTest {

    @Mock
    private EmpleoRepository empleoRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private CategoriaService categoriaService;

    @InjectMocks
    private EmpleoService empleoService;

    private User sampleUser;
    private Empleo sampleEmpleo;
    private Empleo sampleJefe;
    private Categoria sampleCategoria;
    private UUID empleoId;
    private UUID userId;
    private UUID jefeId;
    private UUID categoriaId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        empleoId = UUID.randomUUID();
        jefeId = UUID.randomUUID();
        categoriaId = UUID.randomUUID();

        sampleUser = User.builder()
                .userId(userId)
                .username("recruiter1")
                .email("recruiter@example.com")
                .name("Recruiter Name")
                .build();

        sampleCategoria = Categoria.builder()
                .categoriaId(categoriaId)
                .nombre("Backend")
                .build();

        sampleJefe = Empleo.builder()
                .empleoId(jefeId)
                .nombre("Líder Técnico")
                .empresa("Tecnil")
                .areaTrabajo("Tecnología")
                .nivel("LEAD")
                .sueldo(new BigDecimal("7000.00"))
                .funciones("Liderar equipo de desarrollo")
                .user(sampleUser)
                .build();

        sampleEmpleo = Empleo.builder()
                .empleoId(empleoId)
                .nombre("Desarrollador Java")
                .empresa("Tecnil")
                .areaTrabajo("Tecnología")
                .nivel("SENIOR")
                .sueldo(new BigDecimal("4500.00"))
                .funciones("Desarrollo de microservicios con Spring Boot")
                .cargoJefe(sampleJefe)
                .user(sampleUser)
                .categorias(Set.of(sampleCategoria))
                .build();
    }

    @Test
    void create_WhenValidDTO_ShouldSaveAndReturnEmpleo() {
        EmpleoRequestDTO dto = EmpleoRequestDTO.builder()
                .nombre("Desarrollador Java")
                .empresa("Tecnil")
                .areaTrabajo("Tecnología")
                .nivel("SENIOR")
                .sueldo(new BigDecimal("4500.00"))
                .funciones("Desarrollo de microservicios")
                .userId(userId)
                .cargoJefeId(jefeId)
                .categoriaIds(Set.of(categoriaId))
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(sampleUser));
        when(empleoRepository.findById(jefeId)).thenReturn(Optional.of(sampleJefe));
        when(categoriaRepository.findById(categoriaId)).thenReturn(Optional.of(sampleCategoria));
        when(empleoRepository.save(any(Empleo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Empleo created = empleoService.create(dto);

        assertNotNull(created);
        assertEquals("Desarrollador Java", created.getNombre());
        assertEquals(sampleUser, created.getUser());
        assertEquals(sampleJefe, created.getCargoJefe());
        assertTrue(created.getCategorias().contains(sampleCategoria));
        verify(empleoRepository).save(any(Empleo.class));
    }

    @Test
    void create_WhenUserNotFound_ShouldThrowException() {
        EmpleoRequestDTO dto = EmpleoRequestDTO.builder()
                .userId(userId)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> empleoService.create(dto));
        verify(empleoRepository, never()).save(any());
    }

    @Test
    void create_WhenCargoJefeNotFound_ShouldThrowException() {
        EmpleoRequestDTO dto = EmpleoRequestDTO.builder()
                .userId(userId)
                .cargoJefeId(jefeId)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(sampleUser));
        when(empleoRepository.findById(jefeId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> empleoService.create(dto));
    }

    @Test
    void getEmpleoById_WhenFound_ShouldReturnEmpleo() {
        when(empleoRepository.findById(empleoId)).thenReturn(Optional.of(sampleEmpleo));

        Empleo found = empleoService.getEmpleoById(empleoId);

        assertEquals(sampleEmpleo, found);
    }

    @Test
    void getEmpleoById_WhenNotFound_ShouldThrowException() {
        when(empleoRepository.findById(empleoId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> empleoService.getEmpleoById(empleoId));
    }

    @Test
    void update_WhenSettingSelfAsCargoJefe_ShouldThrowException() {
        EmpleoRequestDTO dto = EmpleoRequestDTO.builder()
                .cargoJefeId(empleoId)
                .build();

        when(empleoRepository.findById(empleoId)).thenReturn(Optional.of(sampleEmpleo));

        assertThrows(IllegalArgumentException.class, () -> empleoService.update(empleoId, dto));
    }

    @Test
    void delete_WhenExists_ShouldDelete() {
        when(empleoRepository.existsById(empleoId)).thenReturn(true);

        empleoService.delete(empleoId);

        verify(empleoRepository).deleteById(empleoId);
    }

    @Test
    void delete_WhenNotExists_ShouldThrowException() {
        when(empleoRepository.existsById(empleoId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> empleoService.delete(empleoId));
    }
}
