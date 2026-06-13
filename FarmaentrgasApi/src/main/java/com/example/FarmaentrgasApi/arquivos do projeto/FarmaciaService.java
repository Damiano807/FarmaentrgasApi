package com.example.FarmaentrgasApi.services;


import com.example.FarmaentrgasApi.infrastucture.Repository.FarmaciaRepository;
import com.example.FarmaentrgasApi.infrastucture.models.Farmacia;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FarmaciaService {

    private final FarmaciaRepository farmaciaRepository;

    @Transactional(readOnly = true)
    public List<Farmacia> listarTodas() {
        return farmaciaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Farmacia buscarPorId(Long id) {
        return farmaciaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Farmácia não encontrada: " + id));
    }

    @Transactional(readOnly = true)
    public List<Farmacia> buscarPorNome(String nome) {
        return farmaciaRepository.findByNomeContainingIgnoreCase(nome);
    }

    @Transactional(readOnly = true)
    public List<Farmacia> buscarProximas(Double lat, Double lng, Double raioKm) {
        // 1 grau ≈ 111 km → convertemos km para graus
        Double raioGraus = raioKm / 111.0;
        return farmaciaRepository.findProximas(lat, lng, raioGraus);
    }

    @Transactional(readOnly = true)
    public List<Farmacia> buscarPorMedicamento(String nomeMedicamento) {
        return farmaciaRepository.findByMedicamentoNome(nomeMedicamento);
    }

    @Transactional
    public Farmacia criar(Farmacia farmacia) {
        return farmaciaRepository.save(farmacia);
    }

    @Transactional
    public Farmacia atualizar(Long id, Farmacia dados) {
        Farmacia farmacia = buscarPorId(id);
        farmacia.setNome(dados.getNome());
        farmacia.setTelefone(dados.getTelefone());
        farmacia.setEmail(dados.getEmail());
        farmacia.setImagemUrl(dados.getImagemUrl());
        farmacia.setTempoMedioEntregaMin(dados.getTempoMedioEntregaMin());
        if (dados.getPontoNoMapa() != null) {
            farmacia.setPontoNoMapa(dados.getPontoNoMapa());
        }
        return farmaciaRepository.save(farmacia);
    }

    // Atualiza avaliação da farmácia com nova nota recebida
    @Transactional
    public void atualizarAvaliacao(Long id, Integer novaNota) {
        Farmacia farmacia = buscarPorId(id);
        // Média incremental: novaMedia = (mediaAtual * total + novaNota) / (total + 1)
        // Aqui usamos uma estimativa simples baseada na avaliação atual
        double novaMedia = ((farmacia.getAvaliacao() * 10) + novaNota) / 11.0;
        farmacia.setAvaliacao(Math.round(novaMedia * 10.0) / 10.0);
        farmaciaRepository.save(farmacia);
    }
}