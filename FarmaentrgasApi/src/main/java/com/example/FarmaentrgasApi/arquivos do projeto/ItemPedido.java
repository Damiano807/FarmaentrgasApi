package com.example.FarmaentrgasApi.infrastucture.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "item_pedido")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ItemPedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Quantidade do produto neste item
    @NotNull
    @Min(1)
    @Column(name = "quantidade", nullable = false)
    private Integer quantidade;

    // Preço unitário no momento da compra (snapshot — não muda se o preço mudar depois)
    @NotNull
    @Column(name = "preco_unitario", nullable = false)
    private Double precoUnitario;

    // Total deste item (quantidade × precoUnitario) — calculado automaticamente
    @Column(name = "subtotal", nullable = false)
    private Double subtotal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    @JsonIgnore
    private Pedido pedido;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "medicamento_id", nullable = false)
    private Medicamento medicamento;

    // Calcula subtotal antes de salvar
    @PrePersist
    @PreUpdate
    public void calcularSubtotal() {
        if (quantidade != null && precoUnitario != null) {
            this.subtotal = quantidade * precoUnitario;
        }
    }
}