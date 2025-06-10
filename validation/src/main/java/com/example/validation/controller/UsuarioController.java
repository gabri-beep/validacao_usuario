package com.example.validation.controller;

import com.example.validation.dto.LoginDto;
import com.example.validation.dto.UsuarioDto;
import com.example.validation.entity.Usuario;
import com.example.validation.repository.UsuarioRepository;
import com.example.validation.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@RestController
@RequestMapping("/usuario")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping
    public ResponseEntity<List<UsuarioDto>> getAllUsers(){
        List<Usuario> usuarios = usuarioService.getAllUsers();
        return ResponseEntity.ok(usuarios.stream().map(UsuarioDto::fromUsuario).toList());
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UsuarioDto> createUser(@RequestParam("foto") MultipartFile foto,
                                                 @RequestPart("usuarioDto") UsuarioDto usuarioDto){
        String fotoPath = saveFoto(foto);
        usuarioDto.setFoto(fotoPath);
        UsuarioDto savedUsuarioDto = usuarioService.createUser(usuarioDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUsuarioDto);
    }

    private String saveFoto(MultipartFile foto){
        String fileName = UUID.randomUUID().toString() + "_" + foto.getOriginalFilename();
        String uploadDir = "src/main/resources/pictures/";

        try{
            Files.copy(foto.getInputStream(), Paths.get(uploadDir + fileName));
        } catch (IOException e){
            e.printStackTrace();
            throw new RuntimeException("Falha ao salvar a foto.");
        }
        return uploadDir + fileName;
    }

    @GetMapping("/foto/{username}")
    public ResponseEntity<byte[]> getFoto(@PathVariable String username){
        try {
            Optional<Usuario> usuario = usuarioRepository.findByUsername(username);

            if (usuario.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
                String fotoPath = usuario.get().getFotoPath();
                Path filePath = Paths.get(fotoPath);

                if (!Files.exists(filePath)){
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
                }

                byte[] fotoBytes = Files.readAllBytes(filePath);

                String contentType = Files.probeContentType(filePath);

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .body(fotoBytes);

        } catch (IOException e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDto> updateUser(@PathVariable Long id, @RequestBody UsuarioDto usuarioDto){
        Optional<UsuarioDto> updatedUsuarioDto = usuarioService.updateUser(id,usuarioDto);
        return updatedUsuarioDto.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        return usuarioService.deleteUser(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> authenticateUser(@RequestBody LoginDto loginDto, Locale locale){
        boolean isAuthenticated = usuarioService.authenticateUser(loginDto);

        Map<String, Object> response = new HashMap<>();

        if (isAuthenticated){
            String successMessage = messageSource.getMessage("Login.success", null, locale);
            response.put("status", "success");
            response.put("message", successMessage);
            return ResponseEntity.ok(response);
        } else {
            String failureMessage = messageSource.getMessage("login.failed", null, locale);
            response.put("status", "error");
            response.put("message", failureMessage);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }
}
