package com.tecnil.my_job_api.controller;

import com.tecnil.my_job_api.dto.EmpleoRequestDTO;
import com.tecnil.my_job_api.entity.Empleo;
import com.tecnil.my_job_api.service.EmpleoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmpleoControllerTest {

    @Mock
    private EmpleoService empleoService;

    @InjectMocks
    private EmpleoController empleoController;

    private Empleo sampleEmpleo;
    private UUID sampleId;

    @BeforeEach
    void setUp() {
        sampleId = UUID.randomUUID();
        sampleEmpleo = Empleo.builder()
                .empleoId(sampleId)
                .nombre("Desarrollador Java")
                .empresa("Tecnil")
                .areaTrabajo("Tecnología")
                .nivel("SENIOR")
                .sueldo(new BigDecimal("4500.00"))
                .funciones("Microservicios Spring Boot")
                .build();
    }

    @Test
    void create_ShouldReturn201CreatedAndLocationHeader() {
        EmpleoRequestDTO dto = EmpleoRequestDTO.builder()
                .nombre("Desarrollador Java")
                .empresa("Tecnil")
                .areaTrabajo("Tecnología")
                .nivel("SENIOR")
                .sueldo(new BigDecimal("4500.00"))
                .funciones("Microservicios Spring Boot")
                .userId(UUID.randomUUID())
                .build();

        when(empleoService.create(any(EmpleoRequestDTO.class))).thenReturn(sampleEmpleo);

        ResponseEntity<Empleo> response = empleoController.create(dto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getHeaders().getLocation());
        assertEquals("/api/v1/empleo/" + sampleId, response.getHeaders().getLocation().getPath());
        assertEquals(sampleEmpleo, response.getBody());
    }

    @Test
    void getAll_WithoutFilters_ShouldReturnAll() {
        when(empleoService.findAll()).thenReturn(List.of(sampleEmpleo));

        ResponseEntity<List<Empleo>> response = empleoController.getAll(null, null, null, null, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(empleoService).findAll();
    }

    @Test
    void getAll_WithEmpresaFilter_ShouldFilterByEmpresa() {
        when(empleoService.findByEmpresa("Tecnil")).thenReturn(List.of(sampleEmpleo));

        ResponseEntity<List<Empleo>> response = empleoController.getAll("Tecnil", null, null, null, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(empleoService).findByEmpresa("Tecnil");
    }

    @Test
    void getById_ShouldReturnEmpleo() {
        when(empleoService.getEmpleoById(sampleId)).thenReturn(sampleEmpleo);

        ResponseEntity<Empleo> response = empleoController.getById(sampleId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(sampleEmpleo, response.getBody());
    }

    @Test
    void update_ShouldReturnUpdatedEmpleo() {
        EmpleoRequestDTO dto = EmpleoRequestDTO.builder()
                .nombre("Desarrollador Java Tech Lead")
                .build();

        when(empleoService.update(eq(sampleId), any(EmpleoRequestDTO.class))).thenReturn(sampleEmpleo);

        ResponseEntity<Empleo> response = empleoController.update(sampleId, dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(sampleEmpleo, response.getBody());
    }

    @Test
    void delete_ShouldReturn204NoContent() {
        doNothing().when(empleoService).delete(sampleId);

        ResponseEntity<Void> response = empleoController.delete(sampleId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(empleoService).delete(sampleId);
    }
}
