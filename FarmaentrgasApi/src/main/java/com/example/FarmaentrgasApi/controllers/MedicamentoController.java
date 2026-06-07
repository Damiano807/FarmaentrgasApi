package com.example.FarmaentrgasApi.controllers;


import com.example.FarmaentrgasApi.infrastucture.models.Medicamento;
import com.example.FarmaentrgasApi.services.MedicamentoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/medicamento")
@RequiredArgsConstructor
public class MedicamentoController {

    private final MedicamentoService medicamentoService;

    // GET /medicamento — lista todos (com filtros opcionais)
    // Usado pela tela BuscarRemedios e FiltrosScreen
    // Ex: GET /medicamento?nome=dipirona&categoria=Medicamentos&precoMax=1000
    @GetMapping
    public ResponseEntity<List<Medicamento>> buscar(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) Long farmaciaId,
            @RequestParam(required = false) Double precoMax) {
        return ResponseEntity.ok(medicamentoService.buscar(nome, categoria, farmaciaId, precoMax));
    }

    // GET /medicamento/{id} — detalhe do produto (DetalhesProdutoScreen)
    @GetMapping("/{id}")
    public ResponseEntity<Medicamento> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(medicamentoService.buscarPorId(id));
    }

    // GET /medicamento/categorias — lista categorias disponíveis (FiltrosScreen)
    @GetMapping("/categorias")
    public ResponseEntity<List<String>> categorias() {
        return ResponseEntity.ok(medicamentoService.listarCategorias());
    }

    // POST /medicamento?farmaciaId=1 — cadastra medicamento numa farmácia
    @PostMapping
    public ResponseEntity<Medicamento> criar(
            @RequestParam Long farmaciaId,
            @RequestBody @Valid Medicamento medicamento) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(medicamentoService.criar(farmaciaId, medicamento));
    }

    // PUT /medicamento/{id} — atualiza dados do medicamento
    @PutMapping("/{id}")
    public ResponseEntity<Medicamento> atualizar(
            @PathVariable Long id,
            @RequestBody Medicamento dados) {
        return ResponseEntity.ok(medicamentoService.atualizar(id, dados));
    }
}
