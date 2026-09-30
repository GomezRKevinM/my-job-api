package com.tecnil.my_job_api.controller;

import com.tecnil.my_job_api.entity.Categoria;
import com.tecnil.my_job_api.service.CategoriaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/categoria")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @PostMapping("")
    public ResponseEntity<Categoria> create(@Valid @RequestBody Categoria categoria) {
        Categoria created = categoriaService.create(categoria);
        URI location = URI.create("/api/v1/categoria/" + created.getCategoriaId());
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping("")
    public ResponseEntity<List<Categoria>> getAll() {
        return ResponseEntity.ok(categoriaService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Categoria> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(categoriaService.getCategoriaById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        categoriaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
