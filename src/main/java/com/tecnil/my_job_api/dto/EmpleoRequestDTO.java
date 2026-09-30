package com.tecnil.my_job_api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmpleoRequestDTO {

    @NotBlank(message = "El nombre del empleo es obligatorio")
    private String nombre;

    @NotBlank(message = "El área de trabajo es obligatoria")
    @JsonProperty("area_trabajo")
    private String areaTrabajo;

    @NotBlank(message = "La empresa es obligatoria")
    private String empresa;

    @NotBlank(message = "El nivel del empleo es obligatorio")
    private String nivel;

    @NotNull(message = "El sueldo es obligatorio")
    @PositiveOrZero(message = "El sueldo no puede ser negativo")
    private BigDecimal sueldo;

    @NotBlank(message = "Las funciones del cargo son obligatorias")
    private String funciones;

    @JsonProperty("cargojefe_id")
    private UUID cargoJefeId;

    @NotNull(message = "El ID del usuario creador de la oferta es obligatorio")
    @JsonProperty("user_id")
    private UUID userId;

    @JsonProperty("categoria_ids")
    private Set<UUID> categoriaIds;

    @JsonProperty("categoria_nombres")
    private Set<String> categoriaNombres;
}
