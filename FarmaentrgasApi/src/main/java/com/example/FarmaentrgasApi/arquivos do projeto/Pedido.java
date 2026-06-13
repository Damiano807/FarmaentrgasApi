package com.example.FarmaentrgasApi.infrastucture.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pedido")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Data/hora em que o pedido foi criado
    @Column(name = "criado_em", nullable = false)
    @Builder.Default
    private LocalDateTime criadoEm = LocalDateTime.now();

    // Data/hora da última atualização de status
    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private StatusPedido status = StatusPedido.AGUARDANDO;

    @Enumerated(EnumType.STRING)
    @Column(name = "metodo_pagamento")
    private MetodoPagamento metodoPagamento;

    // Subtotal dos produtos
    @Column(name = "subtotal")
    private Double subtotal;

    // Taxa de entrega calculada pela distância
    @Column(name = "taxa_entrega")
    private Double taxaEntrega;

    // Total final = subtotal + taxaEntrega
    @Column(name = "total")
    private Double total;

    // Distância total da entrega em km
    @Column(name = "distancia_km")
    private Double distanciaKm;

    // Observações do cliente (ex: "bater palmas no portão")
    @Column(name = "observacoes", length = 500)
    private String observacoes;

    // Nota dada pelo cliente ao pedido (1 a 5)
    @Column(name = "nota_pedido")
    private Integer notaPedido;

    // Nota dada ao entregador (1 a 5)
    @Column(name = "nota_entregador")
    private Integer notaEntregador;

    // Comentário da avaliação
    @Column(name = "comentario_avaliacao", length = 500)
    private String comentarioAvaliacao;

    // ── Relacionamentos ──────────────────────────────────────────────────────

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Usuario cliente;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "farmacia_id", nullable = false)
    private Farmacia farmacia;

    // Entregador pode ser nulo até alguém aceitar o pedido
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "entregador_id")
    private Entregador entregador;

    // Endereço de entrega (snapshot do endereço do cliente)
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "endereco_entrega_id")
    private PontoMapa enderecoEntrega;

    // Itens do pedido
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ItemPedido> itens = new ArrayList<>();

    // Atualiza timestamp e recalcula totais antes de salvar
    @PrePersist
    @PreUpdate
    public void preUpdate() {
        this.atualizadoEm = LocalDateTime.now();
        if (itens != null && !itens.isEmpty()) {
            this.subtotal = itens.stream()
                    .mapToDouble(i -> i.getSubtotal() != null ? i.getSubtotal() : 0.0)
                    .sum();
            this.total = (subtotal != null ? subtotal : 0.0)
                    + (taxaEntrega != null ? taxaEntrega : 0.0);
        }
    }
}