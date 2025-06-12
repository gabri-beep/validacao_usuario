package com.example.validation.service;

import com.example.validation.dto.LoginDto;
import com.example.validation.dto.UsuarioDto;
import com.example.validation.entity.Usuario;
import com.example.validation.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<Usuario> getAllUsers(){
        return usuarioRepository.findAll();
    }

    public UsuarioDto createUser(UsuarioDto usuarioDto){
        Usuario  usuario = usuarioDto.toUsuario();
        usuario = usuarioRepository.save(usuario);
        return UsuarioDto.fromUsuario(usuario);
    }

    public Optional<UsuarioDto> updateUser(Long id, UsuarioDto usuarioDto){
        Optional<Usuario> usuarioOptional = usuarioRepository.findById(id);
        if (usuarioOptional.isPresent()){
            Usuario usuario = usuarioOptional.get();
            usuario.setNome(usuarioDto.getNome());
            usuario.setSobrenome(usuarioDto.getSobrenome());
            usuario.setPassword(usuarioDto.getPassword());
            Usuario updateUsuario = usuarioRepository.save(usuario);
            return Optional.of(UsuarioDto.fromUsuario(updateUsuario));
        }

        return Optional.empty();
    }

    public boolean deleteUser(Long id){
        if (usuarioRepository.existsById(id)){
            usuarioRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public boolean authenticateUser(LoginDto loginDto){
        return usuarioRepository.findByUsername(loginDto.getUsername())
                .map(usuario -> usuario.getPassword().equals(loginDto.getPassword()))
                .orElse(false);
    }
}
