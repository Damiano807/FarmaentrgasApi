package com.example.FarmaentrgasApi.infrastucture.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "usuario")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome é obrigatório")
    @Column(name = "nome", length = 100, nullable = false)
    private String nome;

    @Email(message = "E-mail inválido")
    @NotBlank(message = "E-mail é obrigatório")
    @Column(name = "email", length = 150, nullable = false, unique = true)
    private String email;

    @NotBlank(message = "Senha é obrigatória")
    @Column(name = "senha", length = 100, nullable = false)
     // Nunca retorna a senha para o app
    private String senha;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    @Builder.Default
    private Perfil tipo = Perfil.CLIENTE;

    @Column(name = "telefone", length = 15)
    private String telefone;

    // Localização atual do usuário (atualizada em tempo real quando ele abre o app)
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "ponto_no_mapa_id")
    private PontoMapa pontoNoMapa;

    // Farmácias que o usuário é cliente
    @JsonIgnore
    @ManyToMany(mappedBy = "clientes")
    @Builder.Default
    private List<Farmacia> farmacias = new ArrayList<>();

    // Pedidos feitos por este usuário
    @JsonIgnore
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Pedido> pedidos = new ArrayList<>();
}