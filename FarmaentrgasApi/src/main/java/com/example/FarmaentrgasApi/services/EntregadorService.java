package com.example.FarmaentrgasApi.services;


import com.example.FarmaentrgasApi.infrastucture.Repository.EntregadorRepository;
import com.example.FarmaentrgasApi.infrastucture.models.Entregador;
import com.example.FarmaentrgasApi.infrastucture.models.PontoMapa;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EntregadorService {

    private final EntregadorRepository entregadorRepository;

    @Transactional(readOnly = true)
    public Entregador buscarPorId(Long id) {
        return entregadorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Entregador não encontrado: " + id));
    }

    @Transactional
    public Entregador criar(Entregador entregador) {
        if (entregadorRepository.existsByEmail(entregador.getEmail())) {
            throw new RuntimeException("E-mail já cadastrado: " + entregador.getEmail());
        }
        return entregadorRepository.save(entregador);
    }

    public Optional<Entregador> login(String email, String senha) {
        return entregadorRepository.findByEmail(email)
                .filter(e -> e.getSenha().equals(senha));
    }

    // O app do entregador chama este endpoint a cada 5 segundos para atualizar GPS
    @Transactional
    public Entregador atualizarLocalizacao(Long id, Double lat, Double lng) {
        Entregador entregador = buscarPorId(id);
        PontoMapa ponto = entregador.getLocalizacaoAtual();
        if (ponto == null) {
            ponto = new PontoMapa();
        }
        ponto.setLatitude(lat);
        ponto.setLongitude(lng);
        entregador.setLocalizacaoAtual(ponto);
        return entregadorRepository.save(entregador);
    }

    // Liga/desliga disponibilidade (entregador abre/fecha o app)
    @Transactional
    public Entregador alterarDisponibilidade(Long id, Boolean disponivel) {
        Entregador entregador = buscarPorId(id);
        entregador.setDisponivel(disponivel);
        return entregadorRepository.save(entregador);
    }

    // Busca entregadores online próximos à farmácia para notificar
    @Transactional(readOnly = true)
    public List<Entregador> buscarDisponiveisProximos(Double lat, Double lng, Double raioKm) {
        Double raioGraus = raioKm / 111.0;
        return entregadorRepository.findDisponiveisProximos(lat, lng, raioGraus);
    }

    // Atualiza avaliação após pedido avaliado
    @Transactional
    public void atualizarAvaliacao(Long id, Integer novaNota) {
        Entregador entregador = buscarPorId(id);
        int total = entregador.getTotalAvaliacoes();
        double novaMedia = ((entregador.getAvaliacao() * total) + novaNota) / (double)(total + 1);
        entregador.setAvaliacao(Math.round(novaMedia * 10.0) / 10.0);
        entregador.setTotalAvaliacoes(total + 1);
        entregadorRepository.save(entregador);
    }
}