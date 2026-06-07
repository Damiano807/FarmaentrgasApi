package com.example.FarmaentrgasApi.infrastucture.Repository;


import com.example.FarmaentrgasApi.infrastucture.models.Farmacia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FarmaciaRepository extends JpaRepository<Farmacia, Long> {

    // Busca farmácias pelo nome (case-insensitive)
    List<Farmacia> findByNomeContainingIgnoreCase(String nome);

    // Busca farmácias que tenham um medicamento com determinado nome
    @Query("""
        SELECT DISTINCT f FROM Farmacia f
        JOIN f.medicamentos m
        WHERE LOWER(m.nome) LIKE LOWER(CONCAT('%', :nomeMedicamento, '%'))
        AND m.disponivel = true
    """)
    List<Farmacia> findByMedicamentoNome(@Param("nomeMedicamento") String nomeMedicamento);

    // Busca farmácias próximas a uma coordenada (raio em graus ≈ km)
    @Query("""
        SELECT f FROM Farmacia f
        WHERE f.pontoNoMapa IS NOT NULL
        AND ABS(f.pontoNoMapa.latitude  - :lat) < :raio
        AND ABS(f.pontoNoMapa.longitude - :lng) < :raio
    """)
    List<Farmacia> findProximas(
            @Param("lat") Double lat,
            @Param("lng") Double lng,
            @Param("raio") Double raio
    );
}
