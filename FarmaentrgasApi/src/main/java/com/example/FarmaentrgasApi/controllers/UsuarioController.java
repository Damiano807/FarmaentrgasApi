package com.example.FarmaentrgasApi.controllers;


import com.example.FarmaentrgasApi.infrastucture.models.Usuario;
import com.example.FarmaentrgasApi.services.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/usuario")
@RequiredArgsConstructor
public class UsuarioController {
    private  final UsuarioService usuarioService;




    // ── POST /usuario  →  Cadastro ────────────────────────────────────────────
    @PostMapping
    public ResponseEntity<Usuario> CriarUsuario(@RequestBody @Valid Usuario usuario) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.criar(usuario));
    }

    // ── POST /usuario/login  →  Login ─────────────────────────────────────────
    @PostMapping("/login")
    public ResponseEntity<Usuario> Login(@RequestBody LoginRequest loginRequest) {
        return usuarioService
                .login(loginRequest.getEmail(), loginRequest.getSenha())
                .map(ResponseEntity::ok)                          // 200 + Usuario
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()); // 401
    }


}
