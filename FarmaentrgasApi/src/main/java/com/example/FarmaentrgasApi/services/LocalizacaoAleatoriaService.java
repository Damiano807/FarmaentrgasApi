package com.example.FarmaentrgasApi.services;

import com.example.FarmaentrgasApi.infrastucture.models.PontoMapa;
import org.springframework.stereotype.Service;

import java.util.Random;

/**
 * Gera uma localização aleatória dentro de Luanda, Angola,
 * para simular a posição do utilizador no mapa ao fazer login.
 *
 * Limites aproximados de Luanda:
 *   Latitude:  -9.00  a  -8.70
 *   Longitude: 13.15  a  13.40
 */
@Service
public class LocalizacaoAleatoriaService {

    // Limites geográficos de Luanda
    private static final double LAT_MIN  = -9.00;
    private static final double LAT_MAX  = -8.70;
    private static final double LNG_MIN  = 13.15;
    private static final double LNG_MAX  = 13.40;

    private final Random random = new Random();

    /**
     * Gera um PontoMapa com coordenadas aleatórias dentro de Luanda.
     */
    public PontoMapa gerarPontoAleatorio() {
        double lat = LAT_MIN + (LAT_MAX - LAT_MIN) * random.nextDouble();
        double lng = LNG_MIN + (LNG_MAX - LNG_MIN) * random.nextDouble();

        // Arredonda para 6 casas decimais (precisão de ~11 cm)
        lat = Math.round(lat * 1_000_000.0) / 1_000_000.0;
        lng = Math.round(lng * 1_000_000.0) / 1_000_000.0;

        PontoMapa ponto = new PontoMapa();
        ponto.setLatitude(lat);
        ponto.setLongitude(lng);
        ponto.setEnderecoFormatado("Ponto gerado automaticamente: " + lat + ", " + lng);
        return ponto;
    }
}
