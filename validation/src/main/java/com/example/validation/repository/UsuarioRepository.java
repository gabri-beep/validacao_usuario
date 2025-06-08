package com.example.validation.repository;

import com.example.validation.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    public Optional<Usuario> findByUserName(String username);
}
