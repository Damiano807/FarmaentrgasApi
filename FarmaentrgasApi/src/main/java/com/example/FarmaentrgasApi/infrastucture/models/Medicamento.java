package com.example.FarmaentrgasApi.infrastucture.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

@Entity
@Table(name = "medicamento")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Medicamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome é obrigatório")
    @Column(name = "nome", length = 200, nullable = false)
    private String nome;

    // Ex: "com 10 comprimidos", "Frasco 20mL"
    @Column(name = "descricao", length = 500)
    private String descricao;

    // Categoria: "Medicamentos", "Vitaminas", "Higiene", etc.
    @Column(name = "categoria", length = 100)
    private String categoria;

    @NotNull(message = "Preço é obrigatório")
    @PositiveOrZero
    @Column(name = "preco", nullable = false)
    private Double preco;

    // Quantidade disponível em estoque
    @Column(name = "estoque")
    @Builder.Default
    private Integer estoque = 0;

    // Avaliação média (0.0 a 5.0)
    @Column(name = "avaliacao")
    @Builder.Default
    private Double avaliacao = 0.0;

    // Total de avaliações recebidas
    @Column(name = "total_avaliacoes")
    @Builder.Default
    private Integer totalAvaliacoes = 0;

    // URL da imagem do produto
    @Column(name = "imagem_url", length = 500)
    private String imagemUrl;

    // Se está disponível para venda
    @Column(name = "disponivel")
    @Builder.Default
    private Boolean disponivel = true;

    // Farmácia que vende este medicamento
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "farmacia_id", nullable = false)
    @JsonIgnore
    private Farmacia farmacia;

    // Campo extra para retornar o ID da farmácia sem circular reference
    @Transient
    public Long getFarmaciaId() {
        return farmacia != null ? farmacia.getId() : null;
    }

    @Transient
    public String getFarmaciaNome() {
        return farmacia != null ? farmacia.getNome() : null;
    }
}