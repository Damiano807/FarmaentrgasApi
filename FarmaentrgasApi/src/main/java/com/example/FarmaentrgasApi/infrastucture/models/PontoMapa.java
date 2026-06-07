package com.example.FarmaentrgasApi.infrastucture.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ponto_mapa")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class PontoMapa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    // Endereço legível (ex: "Rua dos Coqueiros, 123, Maianga")
    @Column(length = 300)
    private String enderecoFormatado;
}