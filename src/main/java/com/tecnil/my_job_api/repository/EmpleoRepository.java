package com.tecnil.my_job_api.repository;

import com.tecnil.my_job_api.entity.Empleo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EmpleoRepository extends JpaRepository<Empleo, UUID> {
    List<Empleo> findByEmpresaIgnoreCase(String empresa);
    List<Empleo> findByAreaTrabajoIgnoreCase(String areaTrabajo);
    List<Empleo> findByNivelIgnoreCase(String nivel);
    List<Empleo> findByUser_UserId(UUID userId);
    List<Empleo> findByCargoJefe_EmpleoId(UUID cargoJefeId);
    List<Empleo> findByCategorias_NombreIgnoreCase(String categoriaNombre);
}
