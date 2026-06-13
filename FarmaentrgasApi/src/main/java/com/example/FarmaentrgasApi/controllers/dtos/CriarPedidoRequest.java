package com.example.FarmaentrgasApi.controllers.dtos;

import com.example.FarmaentrgasApi.infrastucture.models.MetodoPagamento;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * Payload enviado pelo app ao criar um pedido.
 * POST /pedido
 *
 * O campo enderecoEntrega é opcional: se não for fornecido, o sistema
 * usa automaticamente a localização actual do utilizador (atribuída no login).
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

    /**
     * Endereço de entrega explícito (opcional).
     * Se null, o sistema usa a localização actual do utilizador no mapa.
     */
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
