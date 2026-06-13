package com.example.FarmaentrgasApi.controllers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {
    public String email;
    private String senha;

    // Construtor vazio obrigatório para o Jackson deserializar o JSON

}
