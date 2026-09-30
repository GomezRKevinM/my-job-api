package com.tecnil.my_job_api.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "categorias")
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "categoria_id", unique = true, nullable = false)
    @JsonProperty("categoria_id")
    private UUID categoriaId;

    @NotBlank(message = "El nombre de la categoría es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre de la categoría debe tener entre 2 y 100 caracteres")
    @Column(unique = true, nullable = false)
    private String nombre;

    @Column(length = 255)
    private String descripcion;

    // Métodos de conveniencia
    public UUID getId() {
        return this.categoriaId;
    }

    public void setId(UUID id) {
        this.categoriaId = id;
    }
}
