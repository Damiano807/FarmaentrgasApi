package com.example.FarmaentrgasApi.infrastucture.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "farmacia")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Farmacia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome da farmácia é obrigatório")
    @Column(name = "nome", length = 150, nullable = false)
    private String nome;

    @Column(name = "telefone", length = 15)
    private String telefone;

    @Column(name = "email", length = 150)
    private String email;

    // Imagem/logo da farmácia (URL ou base64)
    @Column(name = "imagem_url", length = 500)
    private String imagemUrl;

    // Avaliação média (0.0 a 5.0)
    @Column(name = "avaliacao")
    @Builder.Default
    private Double avaliacao = 0.0;

    // Tempo médio de entrega em minutos
    @Column(name = "tempo_medio_entrega_min")
    @Builder.Default
    private Integer tempoMedioEntregaMin = 60;

    // Localização física da farmácia
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "ponto_no_mapa_id")
    private PontoMapa pontoNoMapa;

    // Medicamentos que esta farmácia vende
    @OneToMany(mappedBy = "farmacia", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Medicamento> medicamentos = new ArrayList<>();

    // Clientes associados a esta farmácia
    @JsonIgnore
    @ManyToMany
    @JoinTable(
            name = "farmacia_cliente",
            joinColumns = @JoinColumn(name = "farmacia_id"),
            inverseJoinColumns = @JoinColumn(name = "usuario_id")
    )
    @Builder.Default
    private List<Usuario> clientes = new ArrayList<>();

    // Pedidos desta farmácia
    @JsonIgnore
    @OneToMany(mappedBy = "farmacia", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Pedido> pedidos = new ArrayList<>();
}