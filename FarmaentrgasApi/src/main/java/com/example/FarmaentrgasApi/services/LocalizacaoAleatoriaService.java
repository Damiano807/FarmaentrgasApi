package com.example.FarmaentrgasApi.services;

import com.example.FarmaentrgasApi.infrastucture.models.PontoMapa;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

/**
 * Gera uma localização aleatória dentro de Luanda, Angola,
 * para simular a posição do utilizador no mapa ao fazer login.
 *
 * Limites aproximados de Luanda:
 *   Latitude:  -9.00  a  -8.70
 *   Longitude: 13.15  a  13.40
 *
 * O endereço formatado gerado é plausível para Luanda
 * (ex: "Quarteirão 3, Bloco B, Casa 7, Bairro Maianga").
 * As coordenadas reais são preservadas para cálculo de distâncias.
 */
@Service
public class LocalizacaoAleatoriaService {

    // Limites geográficos de Luanda
    private static final double LAT_MIN = -9.00;
    private static final double LAT_MAX = -8.70;
    private static final double LNG_MIN = 13.15;
    private static final double LNG_MAX = 13.40;

    // Bairros reais de Luanda
    private static final List<String> BAIRROS = List.of(
            "Maianga", "Ingombota", "Rangel", "Sambizanga", "Kilamba Kiaxi",
            "Cazenga", "Viana", "Cacuaco", "Belas", "Talatona",
            "Morro Bento", "Alvalade", "Patriota", "Camama", "Zango",
            "Rocha Pinto", "Benfica", "Luanda Sul", "Golfe", "Vila Alice"
    );

    // Letras de bloco (A a F são as mais comuns em Luanda)
    private static final List<String> BLOCOS = List.of("A", "B", "C", "D", "E", "F");

    private final Random random = new Random();

    /**
     * Gera um PontoMapa com coordenadas aleatórias dentro de Luanda
     * e um endereço formatado plausível no estilo angolano.
     */
    public PontoMapa gerarPontoAleatorio() {
        double lat = LAT_MIN + (LAT_MAX - LAT_MIN) * random.nextDouble();
        double lng = LNG_MIN + (LNG_MAX - LNG_MIN) * random.nextDouble();

        // Arredonda para 6 casas decimais (precisão de ~11 cm)
        lat = Math.round(lat * 1_000_000.0) / 1_000_000.0;
        lng = Math.round(lng * 1_000_000.0) / 1_000_000.0;

        String enderecoFormatado = gerarEnderecoFormatado();

        PontoMapa ponto = new PontoMapa();
        ponto.setLatitude(lat);
        ponto.setLongitude(lng);
        ponto.setEnderecoFormatado(enderecoFormatado);
        return ponto;
    }

    /**
     * Gera um endereço no formato angolano típico:
     * "Quarteirão N, Bloco X, Casa N, Bairro Y"
     */
    private String gerarEnderecoFormatado() {
        int quarteirao = random.nextInt(20) + 1;          // 1 a 20
        String bloco   = BLOCOS.get(random.nextInt(BLOCOS.size()));
        int casa       = random.nextInt(30) + 1;           // 1 a 30
        String bairro  = BAIRROS.get(random.nextInt(BAIRROS.size()));

        return String.format("Quarteirão %d, Bloco %s, Casa %d, Bairro %s",
                quarteirao, bloco, casa, bairro);
    }
}