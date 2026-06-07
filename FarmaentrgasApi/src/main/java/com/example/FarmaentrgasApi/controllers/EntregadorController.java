package com.example.FarmaentrgasApi.controllers;


import com.example.FarmaentrgasApi.controllers.dtos.LoginEntregadorRequest;
import com.example.FarmaentrgasApi.infrastucture.models.Entregador;
import com.example.FarmaentrgasApi.infrastucture.models.Pedido;
import com.example.FarmaentrgasApi.services.EntregadorService;
import com.example.FarmaentrgasApi.services.PedidoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/entregador")
@RequiredArgsConstructor
public class EntregadorController {

    private final EntregadorService entregadorService;
    private final PedidoService pedidoService;

    // POST /entregador — cadastro do entregador
    @PostMapping
    public ResponseEntity<Entregador> criar(@RequestBody @Valid Entregador entregador) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(entregadorService.criar(entregador));
    }

    // POST /entregador/login
    @PostMapping("/login")
    public ResponseEntity<Entregador> login(@RequestBody LoginEntregadorRequest req) {
        return entregadorService.login(req.getEmail(), req.getSenha())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    // GET /entregador/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Entregador> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(entregadorService.buscarPorId(id));
    }

    // PATCH /entregador/{id}/localizacao — app do entregador envia GPS a cada 5s
    // Body: { "latitude": -8.83, "longitude": 13.23 }
    @PatchMapping("/{id}/localizacao")
    public ResponseEntity<Entregador> atualizarLocalizacao(
            @PathVariable Long id,
            @RequestBody Map<String, Double> body) {
        Double lat = body.get("latitude");
        Double lng = body.get("longitude");
        if (lat == null || lng == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(entregadorService.atualizarLocalizacao(id, lat, lng));
    }

    // PATCH /entregador/{id}/disponibilidade — liga/desliga modo online
    // Body: { "disponivel": true }
    @PatchMapping("/{id}/disponibilidade")
    public ResponseEntity<Entregador> alterarDisponibilidade(
            @PathVariable Long id,
            @RequestBody Map<String, Boolean> body) {
        Boolean disponivel = body.get("disponivel");
        if (disponivel == null) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(entregadorService.alterarDisponibilidade(id, disponivel));
    }

    // GET /entregador/{id}/pedidos — histórico do entregador
    @GetMapping("/{id}/pedidos")
    public ResponseEntity<List<Pedido>> pedidos(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.listarPorEntregador(id));
    }

    // GET /entregador/disponiveis?lat=-8.83&lng=13.23&raio=5
    @GetMapping("/disponiveis")
    public ResponseEntity<List<Entregador>> disponiveis(
            @RequestParam Double lat,
            @RequestParam Double lng,
            @RequestParam(defaultValue = "10.0") Double raio) {
        return ResponseEntity.ok(
                entregadorService.buscarDisponiveisProximos(lat, lng, raio));
    }
}