package com.marketplace.ms_usuarios;

import com.marketplace.ms_usuarios.dto.UsuarioRequestDTO;
import com.marketplace.ms_usuarios.dto.UsuarioResponseDTO;
import com.marketplace.ms_usuarios.model.Rol;
import com.marketplace.ms_usuarios.model.Usuario;
import com.marketplace.ms_usuarios.repository.RolRepository;
import com.marketplace.ms_usuarios.repository.UsuarioRepository;
import com.marketplace.ms_usuarios.service.UsuarioService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para UsuarioService.
 * Patrón Given/When/Then con Mockito (sin BD real).
 */
@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock private UsuarioRepository usuarioRepository;
    @Mock private RolRepository rolRepository;
    @InjectMocks private UsuarioService usuarioService;

    private Usuario usuario;
    private UsuarioRequestDTO dto;

    @BeforeEach
    void setUp() {
        Rol rol = new Rol();
        rol.setId(1L);
        rol.setNombre("COMPRADOR");

        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("Martin");
        usuario.setApellido("Mouat");
        usuario.setEmail("martin@duoc.cl");
        usuario.setTelefono("912345678");
        usuario.setActivo(true);
        usuario.setRol(rol);

        dto = new UsuarioRequestDTO();
        dto.setNombre("Martin");
        dto.setApellido("Mouat");
        dto.setEmail("martin@duoc.cl");
        dto.setPassword("clave123");
        dto.setTelefono("912345678");
    }

    @Test
    @DisplayName("obtenerTodos: debería retornar lista de usuarios activos")
    void shouldReturnAllActiveUsers() {
        // GIVEN
        when(usuarioRepository.findAllActivos()).thenReturn(List.of(usuario));
        // WHEN
        List<UsuarioResponseDTO> resultado = usuarioService.obtenerTodos();
        // THEN
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("martin@duoc.cl", resultado.get(0).getEmail());
    }

    @Test
    @DisplayName("obtenerPorId: debería retornar el usuario cuando existe")
    void shouldReturnUserById() {
        // GIVEN
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        // WHEN
        Optional<UsuarioResponseDTO> resultado = usuarioService.obtenerPorId(1L);
        // THEN
        assertTrue(resultado.isPresent());
        assertEquals("Martin", resultado.get().getNombre());
    }

    @Test
    @DisplayName("obtenerPorId: debería retornar vacío cuando el usuario no existe")
    void shouldReturnEmptyWhenUserNotFound() {
        // GIVEN
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());
        // WHEN
        Optional<UsuarioResponseDTO> resultado = usuarioService.obtenerPorId(99L);
        // THEN
        assertFalse(resultado.isPresent());
    }

    @Test
    @DisplayName("guardar: debería crear el usuario cuando el email no existe")
    void shouldSaveUserSuccessfully() {
        // GIVEN
        when(usuarioRepository.existsByEmail("martin@duoc.cl")).thenReturn(false);
        when(rolRepository.findByNombre("COMPRADOR")).thenReturn(Optional.of(usuario.getRol()));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);
        // WHEN
        UsuarioResponseDTO resultado = usuarioService.guardar(dto);
        // THEN
        assertNotNull(resultado);
        assertEquals("martin@duoc.cl", resultado.getEmail());
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    @DisplayName("guardar: debería lanzar excepción cuando el email ya existe")
    void shouldThrowWhenEmailDuplicated() {
        // GIVEN — regla de negocio: email único
        when(usuarioRepository.existsByEmail("martin@duoc.cl")).thenReturn(true);
        // WHEN + THEN
        RuntimeException ex = assertThrows(RuntimeException.class, () -> usuarioService.guardar(dto));
        assertTrue(ex.getMessage().contains("email"));
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("desactivar: debería marcar el usuario como inactivo (soft delete)")
    void shouldDeactivateUser() {
        // GIVEN
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);
        // WHEN
        usuarioService.desactivar(1L);
        // THEN — verificamos que se guardó con activo=false
        assertFalse(usuario.isActivo());
        verify(usuarioRepository, times(1)).save(usuario);
    }
}
