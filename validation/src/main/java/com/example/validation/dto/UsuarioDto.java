package com.example.validation.dto;

import com.example.validation.entity.Usuario;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
@Data
@AllArgsConstructor
@NoArgsConstructor

public class UsuarioDto implements Serializable {

    private Long id;
    private String username;
    private String password;
    private String nome;
    private String sobrenome;
    private String email;

    public Usuario toUsuario(){
        return new Usuario(
          this.id,
          this.username,
          this.password,
          this.nome,
          this.sobrenome,
          this.email
        );
    }

    public static UsuarioDto fromUsuario(Usuario usuario){
        return new UsuarioDto(
                usuario.getId(),
                usuario.getUsername(),
                usuario.getPassword(),
                usuario.getNome(),
                usuario.getSobrenome(),
                usuario.getEmail()
        );
    }
}
