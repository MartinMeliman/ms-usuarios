package com.marketplace.ms_usuarios.service;
import com.marketplace.ms_usuarios.dto.*;
import com.marketplace.ms_usuarios.model.*;
import com.marketplace.ms_usuarios.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j @Service @RequiredArgsConstructor
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;

    public List<UsuarioResponseDTO> obtenerTodos() {
        log.info("Consultando todos los usuarios activos");
        return usuarioRepository.findAllActivos().stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public Optional<UsuarioResponseDTO> obtenerPorId(Long id) {
        log.info("Buscando usuario ID: {}", id);
        return usuarioRepository.findById(id).map(this::mapToDTO);
    }

    public List<UsuarioResponseDTO> buscarPorNombre(String nombre) {
        return usuarioRepository.buscarPorNombre(nombre).stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public UsuarioResponseDTO guardar(UsuarioRequestDTO dto) {
        // Regla de negocio: email unico
        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            log.warn("Email duplicado: {}", dto.getEmail());
            throw new RuntimeException("Ya existe un usuario con el email: " + dto.getEmail());
        }
        Rol rol = dto.getRolId() != null
            ? rolRepository.findById(dto.getRolId()).orElseThrow(() -> new RuntimeException("Rol no encontrado"))
            : rolRepository.findByNombre("COMPRADOR").orElse(null);

        Usuario u = new Usuario();
        u.setNombre(dto.getNombre()); u.setApellido(dto.getApellido());
        u.setEmail(dto.getEmail()); u.setPassword(dto.getPassword());
        u.setTelefono(dto.getTelefono()); u.setRol(rol);
        log.info("Guardando usuario: {}", dto.getEmail());
        return mapToDTO(usuarioRepository.save(u));
    }

    public Optional<UsuarioResponseDTO> actualizar(Long id, UsuarioRequestDTO dto) {
        return usuarioRepository.findById(id).map(u -> {
            if (!u.getEmail().equals(dto.getEmail()) && usuarioRepository.existsByEmail(dto.getEmail()))
                throw new RuntimeException("El email " + dto.getEmail() + " ya esta en uso");
            u.setNombre(dto.getNombre()); u.setApellido(dto.getApellido());
            u.setEmail(dto.getEmail()); u.setTelefono(dto.getTelefono());
            log.info("Actualizando usuario ID: {}", id);
            return mapToDTO(usuarioRepository.save(u));
        });
    }

    public void desactivar(Long id) {
        // Soft delete: activo = false, no elimina el registro
        usuarioRepository.findById(id).ifPresent(u -> {
            log.info("Desactivando usuario ID: {}", id);
            u.setActivo(false);
            usuarioRepository.save(u);
        });
    }

    private UsuarioResponseDTO mapToDTO(Usuario u) {
        return new UsuarioResponseDTO(u.getId(), u.getNombre(), u.getApellido(),
            u.getEmail(), u.getTelefono(), u.isActivo(),
            u.getRol() != null ? u.getRol().getNombre() : null, u.getCreadoEn());
    }
}
