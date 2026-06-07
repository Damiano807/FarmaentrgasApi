package com.example.FarmaentrgasApi.services;


import com.example.FarmaentrgasApi.infrastucture.models.PontoMapa;
import org.springframework.stereotype.Service;

/**
 * Calcula distâncias e taxas de entrega.
 * Usa a fórmula de Haversine para distância entre coordenadas.
 * No futuro pode chamar Google Maps Directions API para rotas reais.
 */
@Service
public class RotaService {

    // Taxa base de entrega em kwanzas
    private static final double TAXA_BASE = 200.0;

    // Taxa por quilômetro em kwanzas
    private static final double TAXA_POR_KM = 80.0;

    // Raio da Terra em km
    private static final double RAIO_TERRA_KM = 6371.0;

    /**
     * Calcula a distância em km entre dois pontos usando Haversine.
     */
    public double calcularDistanciaKm(PontoMapa origem, PontoMapa destino) {
        double lat1 = Math.toRadians(origem.getLatitude());
        double lat2 = Math.toRadians(destino.getLatitude());
        double dLat = Math.toRadians(destino.getLatitude() - origem.getLatitude());
        double dLng = Math.toRadians(destino.getLongitude() - origem.getLongitude());

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return RAIO_TERRA_KM * c;
    }

    /**
     * Calcula a taxa de entrega com base na distância.
     * Taxa = 200 AOA (base) + 80 AOA × distância em km
     */
    public double calcularTaxaEntrega(double distanciaKm) {
        double taxa = TAXA_BASE + (TAXA_POR_KM * distanciaKm);
        // Arredonda para múltiplo de 50 (mais amigável para o usuário)
        return Math.ceil(taxa / 50.0) * 50.0;
    }

    /**
     * Retorna distância e taxa juntos.
     */
    public ResultadoRota calcularRota(PontoMapa origem, PontoMapa destino) {
        double distancia = calcularDistanciaKm(origem, destino);
        double taxa = calcularTaxaEntrega(distancia);
        // Estimativa de tempo: 30 km/h média em cidade
        int minutos = (int) Math.ceil((distancia / 30.0) * 60);
        return new ResultadoRota(distancia, taxa, minutos);
    }

    public record ResultadoRota(
            double distanciaKm,
            double taxaEntrega,
            int tempoEstimadoMin
    ) {}
}
