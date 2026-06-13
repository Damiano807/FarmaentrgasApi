package com.example.FarmaentrgasApi.controllers;

import com.example.FarmaentrgasApi.controllers.dtos.PontoMapaDTO;
import com.example.FarmaentrgasApi.infrastucture.models.Usuario;
import com.example.FarmaentrgasApi.services.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/usuario")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    // ── POST /usuario  →  Cadastro ────────────────────────────────────────────
    @PostMapping
    public ResponseEntity<Usuario> criarUsuario(@RequestBody @Valid Usuario usuario) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.criar(usuario));
    }

    // ── POST /usuario/login  →  Login + geração de localização aleatória ─────
    // Ao fazer login, o sistema atribui automaticamente uma localização aleatória
    // dentro de Luanda para simular o ponto GPS do utilizador.
    @PostMapping("/login")
    public ResponseEntity<Usuario> login(@RequestBody LoginRequest loginRequest) {
        return usuarioService
                .login(loginRequest.getEmail(), loginRequest.getSenha())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    // ── GET /usuario/{id}  →  Dados do utilizador (inclui pontoNoMapa) ───────
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.buscarPorId(id));
    }

    // ── PATCH /usuario/{id}/localizacao  →  Actualiza GPS do utilizador ──────
    // Body: { "latitude": -8.83, "longitude": 13.23, "enderecoFormatado": "..." }
    @PatchMapping("/{id}/localizacao")
    public ResponseEntity<Usuario> atualizarLocalizacao(
            @PathVariable Long id,
            @RequestBody PontoMapaDTO body) {
        return ResponseEntity.ok(usuarioService.atualizarLocalizacao(
                id, body.getLatitude(), body.getLongitude(), body.getEnderecoFormatado()));
    }
}
