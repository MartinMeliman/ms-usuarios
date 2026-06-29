package com.marketplace.ms_usuarios.config;
import com.marketplace.ms_usuarios.model.*;
import com.marketplace.ms_usuarios.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

// CommandLineRunner: Spring ejecuta run() UNA VEZ al levantar
// count() > 0 evita duplicar si la app se reinicia con datos existentes
@Slf4j @Component @RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final RolRepository rolRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    public void run(String... args) {
        if (rolRepository.count() > 0) {
            log.info(">>> Datos ya cargados. Se omite inicializacion.");
            return;
        }
        log.info(">>> Cargando datos iniciales de ms-usuarios...");
        Rol admin    = rolRepository.save(new Rol(null, "ADMIN",     "Administrador"));
        Rol vendedor = rolRepository.save(new Rol(null, "VENDEDOR",  "Puede publicar productos"));
        Rol comprador= rolRepository.save(new Rol(null, "COMPRADOR", "Puede comprar productos"));
        usuarioRepository.save(new Usuario(null,"Admin","Sistema","admin@marketplace.cl","admin123",null,true,null,null,admin));
        usuarioRepository.save(new Usuario(null,"Juan","Perez","juan@mail.com","pass123",null,true,null,null,vendedor));
        usuarioRepository.save(new Usuario(null,"Maria","Lopez","maria@mail.com","pass123",null,true,null,null,comprador));
        log.info(">>> 3 roles y 3 usuarios cargados OK.");
    }
}
