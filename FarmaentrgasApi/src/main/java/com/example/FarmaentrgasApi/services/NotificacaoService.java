package com.example.FarmaentrgasApi.services;

import com.example.FarmaentrgasApi.infrastucture.Repository.EntregadorRepository;
import com.example.FarmaentrgasApi.infrastucture.models.Entregador;
import com.example.FarmaentrgasApi.infrastucture.models.Pedido;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Serviço de notificação da farmácia para entregadores.
 *
 * Quando um novo pedido é criado, a farmácia notifica todos os entregadores
 * disponíveis e próximos. O sistema ordena os candidatos por distância
 * (usando o grafo) e loga a notificação.
 *
 * Em produção, substituir o log por push notification (FCM / APNs) ou
 * WebSocket para notificação em tempo real.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificacaoService {

    private final EntregadorRepository entregadorRepository;
    private final RotaService rotaService;

    // Raio padrão em km para procurar entregadores próximos à farmácia
    private static final double RAIO_NOTIFICACAO_KM = 10.0;

    /**
     * Notifica entregadores disponíveis próximos à farmácia sobre um novo pedido.
     *
     * @param pedido Pedido recém-criado
     * @return Lista de entregadores notificados, ordenados por proximidade
     */
    public List<Entregador> notificarEntregadoresDisponiveis(Pedido pedido) {

        if (pedido.getFarmacia() == null || pedido.getFarmacia().getPontoNoMapa() == null) {
            log.warn("[Notificação] Pedido #{} sem localização de farmácia — não foi possível notificar entregadores.",
                    pedido.getId());
            return List.of();
        }

        double lat = pedido.getFarmacia().getPontoNoMapa().getLatitude();
        double lng = pedido.getFarmacia().getPontoNoMapa().getLongitude();

        // Busca entregadores disponíveis dentro do raio da farmácia
        double raioGraus = RAIO_NOTIFICACAO_KM / 111.0;
        List<Entregador> candidatos = entregadorRepository.findDisponiveisProximos(lat, lng, raioGraus);

        if (candidatos.isEmpty()) {
            log.info("[Notificação] Pedido #{} — nenhum entregador disponível próximo à farmácia {}.",
                    pedido.getId(), pedido.getFarmacia().getNome());
            return List.of();
        }

        // Ordena candidatos por distância à farmácia (mais próximo primeiro)
        List<Entregador> ordenados = candidatos.stream()
                .filter(e -> e.getLocalizacaoAtual() != null)
                .sorted((a, b) -> {
                    double dA = rotaService.calcularDistanciaKm(
                            pedido.getFarmacia().getPontoNoMapa(), a.getLocalizacaoAtual());
                    double dB = rotaService.calcularDistanciaKm(
                            pedido.getFarmacia().getPontoNoMapa(), b.getLocalizacaoAtual());
                    return Double.compare(dA, dB);
                })
                .toList();

        // Notifica cada entregador (log → substituir por push em produção)
        for (int i = 0; i < ordenados.size(); i++) {
            Entregador e = ordenados.get(i);
            double distancia = rotaService.calcularDistanciaKm(
                    pedido.getFarmacia().getPontoNoMapa(), e.getLocalizacaoAtual());
            log.info("[Notificação] → Entregador #{} ({}) notificado sobre Pedido #{} | " +
                     "Farmácia: {} | Distância: {} km | Prioridade: {}",
                    e.getId(), e.getNome(), pedido.getId(),
                    pedido.getFarmacia().getNome(),
                    Math.round(distancia * 100.0) / 100.0, i + 1);
        }

        log.info("[Notificação] Pedido #{} — {} entregador(es) notificado(s).",
                pedido.getId(), ordenados.size());

        return ordenados;
    }
}
