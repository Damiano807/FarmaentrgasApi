package com.example.FarmaentrgasApi.infrastucture.Repository;

import com.example.FarmaentrgasApi.infrastucture.models.Entregador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EntregadorRepository extends JpaRepository<Entregador, Long> {

    Optional<Entregador> findByEmail(String email);

    boolean existsByEmail(String email);

    // Todos os entregadores disponíveis (online)
    List<Entregador> findByDisponivelTrue();

    // Entregadores disponíveis E próximos (para notificar os mais próximos primeiro)
    @Query("""
        SELECT e FROM Entregador e
        WHERE e.disponivel = true
        AND e.localizacaoAtual IS NOT NULL
        AND ABS(e.localizacaoAtual.latitude  - :lat) < :raio
        AND ABS(e.localizacaoAtual.longitude - :lng) < :raio
    """)
    List<Entregador> findDisponiveisProximos(
            @Param("lat") Double lat,
            @Param("lng") Double lng,
            @Param("raio") Double raio
    );
}