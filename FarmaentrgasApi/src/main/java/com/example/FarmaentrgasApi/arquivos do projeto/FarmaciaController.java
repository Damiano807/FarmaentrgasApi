package com.example.FarmaentrgasApi.controllers;


import com.example.FarmaentrgasApi.infrastucture.models.Farmacia;
import com.example.FarmaentrgasApi.infrastucture.models.Medicamento;
import com.example.FarmaentrgasApi.services.FarmaciaService;
import com.example.FarmaentrgasApi.services.MedicamentoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/farmacia")
@RequiredArgsConstructor
public class FarmaciaController {

    private final FarmaciaService farmaciaService;
    private final MedicamentoService medicamentoService;

    // GET /farmacia — lista todas
    @GetMapping
    public ResponseEntity<List<Farmacia>> listarTodas() {
        return ResponseEntity.ok(farmaciaService.listarTodas());
    }

    // GET /farmacia/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Farmacia> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(farmaciaService.buscarPorId(id));
    }

    // GET /farmacia/buscar?nome=sagrada
    @GetMapping("/buscar")
    public ResponseEntity<List<Farmacia>> buscarPorNome(@RequestParam String nome) {
        return ResponseEntity.ok(farmaciaService.buscarPorNome(nome));
    }

    // GET /farmacia/proximas?lat=-8.83&lng=13.23&raio=5
    @GetMapping("/proximas")
    public ResponseEntity<List<Farmacia>> proximas(
            @RequestParam Double lat,
            @RequestParam Double lng,
            @RequestParam(defaultValue = "5.0") Double raio) {
        return ResponseEntity.ok(farmaciaService.buscarProximas(lat, lng, raio));
    }

    // GET /farmacia/{id}/medicamentos — produtos da farmácia (HomeScreen)
    @GetMapping("/{id}/medicamentos")
    public ResponseEntity<List<Medicamento>> medicamentosDaFarmacia(@PathVariable Long id) {
        return ResponseEntity.ok(medicamentoService.buscar(null, null, id, null));
    }

    // POST /farmacia — cria nova farmácia
    @PostMapping
    public ResponseEntity<Farmacia> criar(@RequestBody @Valid Farmacia farmacia) {
        return ResponseEntity.status(HttpStatus.CREATED).body(farmaciaService.criar(farmacia));
    }

    // PUT /farmacia/{id} — atualiza dados
    @PutMapping("/{id}")
    public ResponseEntity<Farmacia> atualizar(
            @PathVariable Long id, @RequestBody Farmacia dados) {
        return ResponseEntity.ok(farmaciaService.atualizar(id, dados));
    }
}