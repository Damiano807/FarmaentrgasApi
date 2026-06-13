package com.example.FarmaentrgasApi.controllers.dtos;

import lombok.Data;

@Data
public class PontoMapaDTO {
    private Double latitude;
    private Double longitude;
    private String enderecoFormatado;
}