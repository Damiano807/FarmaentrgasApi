package com.example.FarmaentrgasApi.infrastucture.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "entregador")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Entregador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome é obrigatório")
    @Column(name = "nome", length = 100, nullable = false)
    private String nome;

    @Email
    @NotBlank(message = "E-mail é obrigatório")
    @Column(name = "email", length = 150, nullable = false, unique = true)
    private String email;

    @JsonIgnore
    @Column(name = "senha", length = 100, nullable = false)
    private String senha;

    @Column(name = "telefone", length = 15)
    private String telefone;

    // Foto de perfil
    @Column(name = "foto_url", length = 500)
    private String fotoUrl;

    // Avaliação média recebida pelos clientes
    @Column(name = "avaliacao")
    @Builder.Default
    private Double avaliacao = 0.0;

    @Column(name = "total_avaliacoes")
    @Builder.Default
    private Integer totalAvaliacoes = 0;

    // Se está online e disponível para receber pedidos
    @Column(name = "disponivel")
    @Builder.Default
    private Boolean disponivel = false;

    // Localização atual do entregador (atualizada a cada 5 segundos pelo app)
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "ponto_no_mapa_id")
    private PontoMapa localizacaoAtual;

    // Pedidos atribuídos a este entregador
    @JsonIgnore
    @OneToMany(mappedBy = "entregador", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Pedido> pedidos = new ArrayList<>();
}