package com.marketplace.ms_usuarios.dto;
import jakarta.validation.constraints.*;
import lombok.*;

// DTO de ENTRADA: validaciones viven aqui, no en la entidad
@Data @NoArgsConstructor @AllArgsConstructor
public class UsuarioRequestDTO {
    @NotBlank(message = "El nombre no puede estar vacio")
    @Size(min = 2, max = 100)
    private String nombre;
    @NotBlank(message = "El apellido es obligatorio")
    private String apellido;
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Formato de email invalido")
    private String email;
    @NotBlank(message = "La contrasena es obligatoria")
    @Size(min = 6, message = "Minimo 6 caracteres")
    private String password;
    private String telefono;
    private Long rolId;
}
