package com.marketplace.ms_usuarios.dto;
import lombok.*;
import java.time.LocalDateTime;

// DTO de SALIDA: nunca expone el password
@Data @NoArgsConstructor @AllArgsConstructor
public class UsuarioResponseDTO {
    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    private boolean activo;
    private String rol;
    private LocalDateTime creadoEn;
}
