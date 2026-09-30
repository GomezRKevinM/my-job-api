package com.tecnil.my_job_api.controller;

import com.tecnil.my_job_api.dto.EmpleoRequestDTO;
import com.tecnil.my_job_api.entity.Empleo;
import com.tecnil.my_job_api.service.EmpleoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/empleo")
public class EmpleoController {

    private final EmpleoService empleoService;

    public EmpleoController(EmpleoService empleoService) {
        this.empleoService = empleoService;
    }

    @PostMapping("")
    public ResponseEntity<Empleo> create(@Valid @RequestBody EmpleoRequestDTO dto) {
        Empleo created = empleoService.create(dto);
        URI location = URI.create("/api/v1/empleo/" + created.getEmpleoId());
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping("")
    public ResponseEntity<List<Empleo>> getAll(
            @RequestParam(required = false) String empresa,
            @RequestParam(required = false) String areaTrabajo,
            @RequestParam(required = false) String nivel,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) UUID userId) {

        if (empresa != null && !empresa.isBlank()) {
            return ResponseEntity.ok(empleoService.findByEmpresa(empresa));
        }
        if (areaTrabajo != null && !areaTrabajo.isBlank()) {
            return ResponseEntity.ok(empleoService.findByAreaTrabajo(areaTrabajo));
        }
        if (nivel != null && !nivel.isBlank()) {
            return ResponseEntity.ok(empleoService.findByNivel(nivel));
        }
        if (categoria != null && !categoria.isBlank()) {
            return ResponseEntity.ok(empleoService.findByCategoria(categoria));
        }
        if (userId != null) {
            return ResponseEntity.ok(empleoService.findByUserId(userId));
        }

        return ResponseEntity.ok(empleoService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Empleo> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(empleoService.getEmpleoById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Empleo> update(@PathVariable UUID id, @RequestBody EmpleoRequestDTO dto) {
        return ResponseEntity.ok(empleoService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        empleoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
