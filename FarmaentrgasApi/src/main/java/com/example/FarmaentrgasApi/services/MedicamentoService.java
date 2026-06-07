package com.example.FarmaentrgasApi.services;

import com.example.FarmaentrgasApi.infrastucture.Repository.FarmaciaRepository;
import com.example.FarmaentrgasApi.infrastucture.Repository.MedicamentoRepository;
import com.example.FarmaentrgasApi.infrastucture.models.Farmacia;
import com.example.FarmaentrgasApi.infrastucture.models.Medicamento;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicamentoService {

    private final MedicamentoRepository medicamentoRepository;
    private final FarmaciaRepository farmaciaRepository;

    @Transactional(readOnly = true)
    public List<Medicamento> listarTodos() {
        return medicamentoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Medicamento buscarPorId(Long id) {
        return medicamentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Medicamento não encontrado: " + id));
    }

    // Busca para a tela BuscarRemedios — aceita filtros opcionais
    @Transactional(readOnly = true)
    public List<Medicamento> buscar(String nome, String categoria,
                                    Long farmaciaId, Double precoMax) {
        if (nome != null && !nome.isBlank() && precoMax != null) {
            return medicamentoRepository.findByNomeEPrecoMax(nome, precoMax);
        }
        if (nome != null && !nome.isBlank() && farmaciaId != null) {
            return medicamentoRepository.findByNomeContainingIgnoreCaseAndFarmacia_IdAndDisponivelTrue(nome, farmaciaId);
        }
        if (nome != null && !nome.isBlank()) {
            return medicamentoRepository.findByNomeContainingIgnoreCaseAndDisponivelTrue(nome);
        }
        if (categoria != null && !categoria.isBlank()) {
            return medicamentoRepository.findByCategoriaAndDisponivelTrue(categoria);
        }
        if (farmaciaId != null) {
            return medicamentoRepository.findByFarmacia_IdAndDisponivelTrue(farmaciaId);
        }
        return medicamentoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<String> listarCategorias() {
        return medicamentoRepository.findCategoriasDisponiveis();
    }

    @Transactional
    public Medicamento criar(Long farmaciaId, Medicamento medicamento) {
        Farmacia farmacia = farmaciaRepository.findById(farmaciaId)
                .orElseThrow(() -> new RuntimeException("Farmácia não encontrada: " + farmaciaId));
        medicamento.setFarmacia(farmacia);
        return medicamentoRepository.save(medicamento);
    }

    @Transactional
    public Medicamento atualizar(Long id, Medicamento dados) {
        Medicamento med = buscarPorId(id);
        med.setNome(dados.getNome());
        med.setDescricao(dados.getDescricao());
        med.setCategoria(dados.getCategoria());
        med.setPreco(dados.getPreco());
        med.setEstoque(dados.getEstoque());
        med.setImagemUrl(dados.getImagemUrl());
        med.setDisponivel(dados.getDisponivel());
        return medicamentoRepository.save(med);
    }

    // Atualiza avaliação após pedido avaliado
    @Transactional
    public void atualizarAvaliacao(Long id, Integer novaNota) {
        Medicamento med = buscarPorId(id);
        int total = med.getTotalAvaliacoes();
        double novaMedia = ((med.getAvaliacao() * total) + novaNota) / (double)(total + 1);
        med.setAvaliacao(Math.round(novaMedia * 10.0) / 10.0);
        med.setTotalAvaliacoes(total + 1);
        medicamentoRepository.save(med);
    }

    // Baixa estoque após pedido confirmado
    @Transactional
    public void baixarEstoque(Long id, Integer quantidade) {
        Medicamento med = buscarPorId(id);
        int novoEstoque = med.getEstoque() - quantidade;
        if (novoEstoque < 0) {
            throw new RuntimeException("Estoque insuficiente para: " + med.getNome());
        }
        med.setEstoque(novoEstoque);
        if (novoEstoque == 0) med.setDisponivel(false);
        medicamentoRepository.save(med);
    }
}