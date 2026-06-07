package com.example.FarmaentrgasApi.infrastucture.Repository;

import com.example.FarmaentrgasApi.infrastucture.models.Pedido;
import com.example.FarmaentrgasApi.infrastucture.models.StatusPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    // Pedidos de um cliente específico (histórico)
    List<Pedido> findByClienteIdOrderByCriadoEmDesc(Long clienteId);

    // Pedidos de uma farmácia específica
    List<Pedido> findByFarmaciaIdOrderByCriadoEmDesc(Long farmaciaId);

    // Pedidos de um entregador específico
    List<Pedido> findByEntregadorIdOrderByCriadoEmDesc(Long entregadorId);

    // Pedidos aguardando entregador (para notificar entregadores)
    List<Pedido> findByStatusOrderByCriadoEmAsc(StatusPedido status);

    // Pedidos ativos de um entregador (para calcular rota multi-parada)
    @Query("""
        SELECT p FROM Pedido p
        WHERE p.entregador.id = :entregadorId
        AND p.status IN ('EM_SEPARACAO', 'SAIU_ENTREGA')
        ORDER BY p.criadoEm ASC
    """)
    List<Pedido> findPedidosAtivosDoEntregador(@Param("entregadorId") Long entregadorId);

    // Pedidos de um cliente com status específico (para AcompanharPedido)
    List<Pedido> findByClienteIdAndStatus(Long clienteId, StatusPedido status);
}
