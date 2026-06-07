package com.example.FarmaentrgasApi.infrastucture.Repository;

import com.example.FarmaentrgasApi.infrastucture.models.Medicamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicamentoRepository extends JpaRepository<Medicamento, Long> {

    // Busca por nome (autocomplete na tela BuscarRemedios)
    List<Medicamento> findByNomeContainingIgnoreCaseAndDisponivelTrue(String nome);

    // Busca por categoria
    List<Medicamento> findByCategoriaAndDisponivelTrue(String categoria);

    // CORRIGIDO: Busca por farmácia (Adicionado o underscore antes do Id)
    List<Medicamento> findByFarmacia_IdAndDisponivelTrue(Long farmaciaId);

    // CORRIGIDO: Busca por nome E farmácia (Descomentado e ajustado com underscore)
    List<Medicamento> findByNomeContainingIgnoreCaseAndFarmacia_IdAndDisponivelTrue(String nome, Long farmaciaId);

    // Busca com filtro de preço máximo
    @Query("""
        SELECT m FROM Medicamento m
        WHERE LOWER(m.nome) LIKE LOWER(CONCAT('%', :nome, '%'))
        AND m.disponivel = true
        AND m.preco <= :precoMax
    """)
    List<Medicamento> findByNomeEPrecoMax(
            @Param("nome") String nome,
            @Param("precoMax") Double precoMax
    );

    // Categorias distintas disponíveis (para os filtros)
    @Query("SELECT DISTINCT m.categoria FROM Medicamento m WHERE m.disponivel = true")
    List<String> findCategoriasDisponiveis();
}