package com.tecnil.my_job_api.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "empleos")
public class Empleo {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "empleo_id", unique = true, nullable = false)
    @JsonProperty("id")
    private UUID empleoId;

    @NotBlank(message = "El nombre del empleo es obligatorio")
    @Column(nullable = false)
    private String nombre;

    @NotBlank(message = "El área de trabajo es obligatoria")
    @Column(name = "area_trabajo", nullable = false)
    @JsonProperty("area_trabajo")
    private String areaTrabajo;

    @NotBlank(message = "La empresa es obligatoria")
    @Column(nullable = false)
    private String empresa;

    @NotBlank(message = "El nivel del empleo es obligatorio")
    @Column(nullable = false)
    private String nivel;

    @NotNull(message = "El sueldo es obligatorio")
    @PositiveOrZero(message = "El sueldo no puede ser negativo")
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal sueldo;

    @NotBlank(message = "Las funciones del cargo son obligatorias")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String funciones;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cargojefe_id")
    @JsonIgnoreProperties({"cargoJefe", "user", "categorias"})
    @JsonProperty("cargojefe")
    private Empleo cargoJefe;

    @Column(name = "created_at", nullable = false, updatable = false)
    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"password", "createdAt", "updatedAt"})
    private User user;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "empleos_categorias",
            joinColumns = @JoinColumn(name = "empleo_id"),
            inverseJoinColumns = @JoinColumn(name = "categoria_id")
    )
    @Builder.Default
    private Set<Categoria> categorias = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    // Métodos de conveniencia y compatibilidad
    public UUID getId() {
        return this.empleoId;
    }

    public void setId(UUID id) {
        this.empleoId = id;
    }

    public void addCategoria(Categoria categoria) {
        if (this.categorias == null) {
            this.categorias = new HashSet<>();
        }
        this.categorias.add(categoria);
    }

    public void removeCategoria(Categoria categoria) {
        if (this.categorias != null) {
            this.categorias.remove(categoria);
        }
    }
}
