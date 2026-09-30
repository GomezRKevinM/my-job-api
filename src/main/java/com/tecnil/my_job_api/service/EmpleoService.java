package com.tecnil.my_job_api.service;

import com.tecnil.my_job_api.dto.EmpleoRequestDTO;
import com.tecnil.my_job_api.entity.Categoria;
import com.tecnil.my_job_api.entity.Empleo;
import com.tecnil.my_job_api.entity.User;
import com.tecnil.my_job_api.exception.ResourceNotFoundException;
import com.tecnil.my_job_api.repository.CategoriaRepository;
import com.tecnil.my_job_api.repository.EmpleoRepository;
import com.tecnil.my_job_api.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class EmpleoService {

    private final EmpleoRepository empleoRepository;
    private final UserRepository userRepository;
    private final CategoriaRepository categoriaRepository;
    private final CategoriaService categoriaService;

    public EmpleoService(EmpleoRepository empleoRepository,
                         UserRepository userRepository,
                         CategoriaRepository categoriaRepository,
                         CategoriaService categoriaService) {
        this.empleoRepository = empleoRepository;
        this.userRepository = userRepository;
        this.categoriaRepository = categoriaRepository;
        this.categoriaService = categoriaService;
    }

    @Transactional
    public Empleo create(EmpleoRequestDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("La solicitud de empleo no puede ser nula");
        }

        // 1. Validar usuario creador de la oferta
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario creador no encontrado con ID: " + dto.getUserId()));

        // 2. Validar cargo jefe si se proporcionó
        Empleo cargoJefe = null;
        if (dto.getCargoJefeId() != null) {
            cargoJefe = empleoRepository.findById(dto.getCargoJefeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Cargo jefe no encontrado con ID: " + dto.getCargoJefeId()));
        }

        // 3. Resolver categorías
        Set<Categoria> categorias = resolveCategorias(dto.getCategoriaIds(), dto.getCategoriaNombres());

        // 4. Construir y guardar entidad
        Empleo empleo = Empleo.builder()
                .nombre(dto.getNombre())
                .areaTrabajo(dto.getAreaTrabajo())
                .empresa(dto.getEmpresa())
                .nivel(dto.getNivel())
                .sueldo(dto.getSueldo())
                .funciones(dto.getFunciones())
                .cargoJefe(cargoJefe)
                .user(user)
                .categorias(categorias)
                .build();

        return empleoRepository.save(empleo);
    }

    @Transactional
    public Empleo create(Empleo empleo) {
        if (empleo == null) {
            throw new IllegalArgumentException("El empleo no puede ser nulo");
        }
        if (empleo.getUser() == null || empleo.getUser().getUserId() == null) {
            throw new IllegalArgumentException("El usuario creador de la oferta es obligatorio");
        }

        User user = userRepository.findById(empleo.getUser().getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + empleo.getUser().getUserId()));
        empleo.setUser(user);

        if (empleo.getCargoJefe() != null && empleo.getCargoJefe().getEmpleoId() != null) {
            Empleo jefe = empleoRepository.findById(empleo.getCargoJefe().getEmpleoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Cargo jefe no encontrado con ID: " + empleo.getCargoJefe().getEmpleoId()));
            empleo.setCargoJefe(jefe);
        }

        empleo.setEmpleoId(null);
        return empleoRepository.save(empleo);
    }

    @Transactional(readOnly = true)
    public List<Empleo> findAll() {
        return empleoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Empleo> findById(UUID id) {
        if (id == null) {
            return Optional.empty();
        }
        return empleoRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Empleo getEmpleoById(UUID id) {
        return findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empleo no encontrado con ID: " + id));
    }

    @Transactional(readOnly = true)
    public List<Empleo> findByEmpresa(String empresa) {
        return empleoRepository.findByEmpresaIgnoreCase(empresa);
    }

    @Transactional(readOnly = true)
    public List<Empleo> findByAreaTrabajo(String areaTrabajo) {
        return empleoRepository.findByAreaTrabajoIgnoreCase(areaTrabajo);
    }

    @Transactional(readOnly = true)
    public List<Empleo> findByNivel(String nivel) {
        return empleoRepository.findByNivelIgnoreCase(nivel);
    }

    @Transactional(readOnly = true)
    public List<Empleo> findByUserId(UUID userId) {
        return empleoRepository.findByUser_UserId(userId);
    }

    @Transactional(readOnly = true)
    public List<Empleo> findByCategoria(String categoria) {
        return empleoRepository.findByCategorias_NombreIgnoreCase(categoria);
    }

    @Transactional
    public Empleo update(UUID id, EmpleoRequestDTO dto) {
        Empleo existing = getEmpleoById(id);

        if (dto.getNombre() != null && !dto.getNombre().isBlank()) {
            existing.setNombre(dto.getNombre());
        }
        if (dto.getAreaTrabajo() != null && !dto.getAreaTrabajo().isBlank()) {
            existing.setAreaTrabajo(dto.getAreaTrabajo());
        }
        if (dto.getEmpresa() != null && !dto.getEmpresa().isBlank()) {
            existing.setEmpresa(dto.getEmpresa());
        }
        if (dto.getNivel() != null && !dto.getNivel().isBlank()) {
            existing.setNivel(dto.getNivel());
        }
        if (dto.getSueldo() != null) {
            existing.setSueldo(dto.getSueldo());
        }
        if (dto.getFunciones() != null && !dto.getFunciones().isBlank()) {
            existing.setFunciones(dto.getFunciones());
        }

        // Actualizar cargo jefe
        if (dto.getCargoJefeId() != null) {
            if (dto.getCargoJefeId().equals(id)) {
                throw new IllegalArgumentException("Un empleo no puede ser su propio cargo jefe");
            }
            Empleo jefe = empleoRepository.findById(dto.getCargoJefeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Cargo jefe no encontrado con ID: " + dto.getCargoJefeId()));
            existing.setCargoJefe(jefe);
        }

        // Actualizar categorías si se proporcionan
        if (dto.getCategoriaIds() != null || dto.getCategoriaNombres() != null) {
            Set<Categoria> nuevasCategorias = resolveCategorias(dto.getCategoriaIds(), dto.getCategoriaNombres());
            existing.setCategorias(nuevasCategorias);
        }

        return empleoRepository.save(existing);
    }

    @Transactional
    public void delete(UUID id) {
        if (!empleoRepository.existsById(id)) {
            throw new ResourceNotFoundException("No se puede eliminar: empleo no encontrado con ID: " + id);
        }
        empleoRepository.deleteById(id);
    }

    private Set<Categoria> resolveCategorias(Set<UUID> ids, Set<String> nombres) {
        Set<Categoria> categorias = new HashSet<>();

        if (ids != null) {
            for (UUID catId : ids) {
                Categoria cat = categoriaRepository.findById(catId)
                        .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con ID: " + catId));
                categorias.add(cat);
            }
        }

        if (nombres != null) {
            for (String nombre : nombres) {
                if (nombre != null && !nombre.isBlank()) {
                    Categoria cat = categoriaService.findOrCreate(nombre);
                    categorias.add(cat);
                }
            }
        }

        return categorias;
    }
}
