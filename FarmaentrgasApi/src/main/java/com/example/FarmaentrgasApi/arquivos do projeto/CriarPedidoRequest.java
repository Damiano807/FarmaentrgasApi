package com.example.FarmaentrgasApi.controllers.dtos;

import com.example.FarmaentrgasApi.infrastucture.models.MetodoPagamento;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * Payload enviado pelo app ao criar um pedido.
 * POST /pedido
 */
@Data
public class CriarPedidoRequest {

    @NotNull(message = "ID do cliente é obrigatório")
    private Long clienteId;

    @NotNull(message = "ID da farmácia é obrigatório")
    private Long farmaciaId;

    @NotEmpty(message = "O pedido precisa ter ao menos 1 item")
    private List<ItemRequest> itens;

    @NotNull(message = "Método de pagamento é obrigatório")
    private MetodoPagamento metodoPagamento;

    // Localização de entrega informada pelo cliente
    @NotNull(message = "Endereço de entrega é obrigatório")
    private PontoMapaDTO enderecoEntrega;

    private String observacoes;

    @Data
    public static class ItemRequest {
        @NotNull
        private Long medicamentoId;
        @NotNull
        private Integer quantidade;
    }
}
