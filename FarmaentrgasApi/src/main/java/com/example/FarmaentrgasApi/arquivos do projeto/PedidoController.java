package com.example.FarmaentrgasApi.controllers;


import com.example.FarmaentrgasApi.controllers.dtos.*;

import com.example.FarmaentrgasApi.infrastucture.models.Pedido;
import com.example.FarmaentrgasApi.infrastucture.models.StatusPedido;
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
@RequestMapping("/pedido")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService pedidoService;

    // POST /pedido — cliente finaliza compra (ConfirmarPedidoScreen)
    @PostMapping
    public ResponseEntity<Pedido> criar(@RequestBody @Valid CriarPedidoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(pedidoService.criar(request));
    }

    // GET /pedido/{id} — detalhe / acompanhamento (AcompanharPedidoScreen)
    @GetMapping("/{id}")
    public ResponseEntity<Pedido> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.buscarPorId(id));
    }

    // GET /pedido/cliente/{clienteId} — histórico do cliente (PerfilScreen → Meus Pedidos)
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<Pedido>> porCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(pedidoService.listarPorCliente(clienteId));
    }

    // GET /pedido/farmacia/{farmaciaId} — pedidos recebidos pela farmácia
    @GetMapping("/farmacia/{farmaciaId}")
    public ResponseEntity<List<Pedido>> porFarmacia(@PathVariable Long farmaciaId) {
        return ResponseEntity.ok(pedidoService.listarPorFarmacia(farmaciaId));
    }

    // GET /pedido/aguardando — pedidos sem entregador (app do entregador lista para aceitar)
    @GetMapping("/aguardando")
    public ResponseEntity<List<Pedido>> aguardando() {
        return ResponseEntity.ok(pedidoService.listarAguardando());
    }

    // PATCH /pedido/{id}/aceitar?entregadorId=2 — entregador aceita o pedido
    @PatchMapping("/{id}/aceitar")
    public ResponseEntity<Pedido> aceitar(
            @PathVariable Long id,
            @RequestParam Long entregadorId) {
        return ResponseEntity.ok(pedidoService.aceitarPedido(id, entregadorId));
    }

    // PATCH /pedido/{id}/status — farmácia ou entregador atualiza o status
    // Body: { "status": "SAIU_ENTREGA" }
    @PatchMapping("/{id}/status")
    public ResponseEntity<Pedido> atualizarStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        try {
            StatusPedido novoStatus = StatusPedido.valueOf(body.get("status"));
            return ResponseEntity.ok(pedidoService.atualizarStatus(id, novoStatus));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // PATCH /pedido/{id}/cancelar — cliente cancela o pedido (AcompanharPedidoScreen)
    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<Pedido> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.cancelar(id));
    }

    // POST /pedido/{id}/avaliar — cliente avalia após entrega (AvaliarPedidoScreen)
    @PostMapping("/{id}/avaliar")
    public ResponseEntity<Pedido> avaliar(
            @PathVariable Long id,
            @RequestBody @Valid AvaliacaoRequest request) {
        return ResponseEntity.ok(pedidoService.avaliar(id, request));
    }
}
