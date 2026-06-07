package com.example.FarmaentrgasApi.controllers.dtos;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Payload para avaliar um pedido após entrega.
 * POST /pedido/{id}/avaliar
 */
@Data
public class AvaliacaoRequest {

    @NotNull
    @Min(1) @Max(5)
    private Integer notaPedido;

    @Min(1) @Max(5)
    private Integer notaEntregador;

    private String comentario;
}
