package com.example.FarmaentrgasApi.services;

import com.example.FarmaentrgasApi.infrastucture.models.Pedido;
import com.example.FarmaentrgasApi.infrastucture.models.PontoMapa;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Serviço de Grafos para otimização de rota de entregas.
 *
 * Implementa o algoritmo do Vizinho Mais Próximo (Nearest Neighbour Heuristic)
 * para resolver o Problema do Caixeiro Viajante (TSP) de forma eficiente.
 *
 * Fluxo:
 *  1. Recebe a posição atual do entregador + lista de pedidos com endereços.
 *  2. Constrói um grafo completo onde os vértices são: posição do entregador
 *     e os endereços de entrega de cada pedido.
 *  3. Aplica o algoritmo do vizinho mais próximo a partir da posição do entregador.
 *  4. Retorna os pedidos ordenados pela melhor sequência de entregas.
 *
 * Complexidade: O(n²) — aceitável para n < 50 pedidos simultâneos.
 */
@Service
@RequiredArgsConstructor
public class GrafoRotaService {

    private final RotaService rotaService;

    /**
     * Resultado da otimização de rota.
     *
     * @param pedidosOrdenados  Pedidos na sequência ideal de entrega (1º = mais próximo)
     * @param distanciaTotalKm  Soma das distâncias entre todas as paradas
     * @param tempoEstimadoMin  Tempo total estimado considerando 30 km/h
     * @param grafoArestas      Mapa de adjacência: pontoId → lista de (destinoId, distância)
     */
    public record ResultadoRota(
            List<Pedido>         pedidosOrdenados,
            double               distanciaTotalKm,
            int                  tempoEstimadoMin,
            List<ArestaRota>     grafoArestas
    ) {}

    /**
     * Representa uma aresta no grafo de rotas.
     */
    public record ArestaRota(
            String  origem,
            String  destino,
            double  distanciaKm
    ) {}

    /**
     * Ordena os pedidos pela melhor rota a partir da localização do entregador.
     *
     * @param posicaoEntregador  Coordenadas atuais do entregador
     * @param pedidos            Lista de pedidos com endereçoEntrega preenchido
     * @return ResultadoRota com a sequência otimizada
     */
    public ResultadoRota calcularMelhorRota(PontoMapa posicaoEntregador, List<Pedido> pedidos) {

        if (pedidos == null || pedidos.isEmpty()) {
            return new ResultadoRota(Collections.emptyList(), 0.0, 0, Collections.emptyList());
        }

        // Filtra pedidos que têm endereço de entrega definido
        List<Pedido> pedidosValidos = pedidos.stream()
                .filter(p -> p.getEnderecoEntrega() != null)
                .toList();

        if (pedidosValidos.isEmpty()) {
            return new ResultadoRota(pedidos, 0.0, 0, Collections.emptyList());
        }

        // ── 1. Construção do grafo (matriz de distâncias) ──────────────────────
        // Vértice 0 = entregador; vértices 1..n = pedidos
        int n = pedidosValidos.size();
        double[][] dist = new double[n + 1][n + 1];

        // Ponto 0: entregador
        for (int i = 0; i < n; i++) {
            PontoMapa destino = pedidosValidos.get(i).getEnderecoEntrega();
            double d = rotaService.calcularDistanciaKm(posicaoEntregador, destino);
            dist[0][i + 1] = d;
            dist[i + 1][0] = d;
        }

        // Pontos 1..n: distâncias entre pedidos
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                PontoMapa a = pedidosValidos.get(i).getEnderecoEntrega();
                PontoMapa b = pedidosValidos.get(j).getEnderecoEntrega();
                double d = rotaService.calcularDistanciaKm(a, b);
                dist[i + 1][j + 1] = d;
                dist[j + 1][i + 1] = d;
            }
        }

        // ── 2. Algoritmo do Vizinho Mais Próximo (Nearest Neighbour) ──────────
        boolean[] visitado = new boolean[n + 1];
        int[] rota = new int[n + 1];  // índices dos vértices na ordem visitada
        rota[0] = 0;                   // começa no entregador
        visitado[0] = true;

        for (int passo = 1; passo <= n; passo++) {
            int atual = rota[passo - 1];
            double menorDist = Double.MAX_VALUE;
            int proximo = -1;

            for (int j = 1; j <= n; j++) {
                if (!visitado[j] && dist[atual][j] < menorDist) {
                    menorDist = dist[atual][j];
                    proximo = j;
                }
            }

            rota[passo] = proximo;
            visitado[proximo] = true;
        }

        // ── 3. Monta resultado com pedidos na sequência otimizada ─────────────
        List<Pedido>    pedidosOrdenados = new ArrayList<>();
        List<ArestaRota> arestas         = new ArrayList<>();
        double           distTotal       = 0.0;

        String[] nomes = new String[n + 1];
        nomes[0] = "Entregador";
        for (int i = 0; i < n; i++) {
            nomes[i + 1] = "Pedido #" + pedidosValidos.get(i).getId();
        }

        for (int passo = 1; passo <= n; passo++) {
            int idx = rota[passo];
            Pedido pedido = pedidosValidos.get(idx - 1);
            pedidosOrdenados.add(pedido);

            double trecho = dist[rota[passo - 1]][idx];
            distTotal += trecho;

            arestas.add(new ArestaRota(
                    nomes[rota[passo - 1]],
                    nomes[idx],
                    Math.round(trecho * 100.0) / 100.0
            ));
        }

        // Tempo estimado: 30 km/h média urbana
        int tempoMin = (int) Math.ceil((distTotal / 30.0) * 60);

        return new ResultadoRota(
                pedidosOrdenados,
                Math.round(distTotal * 100.0) / 100.0,
                tempoMin,
                arestas
        );
    }

    /**
     * Calcula distância entre posição do entregador e um único pedido.
     * Usado para ordenar pedidos de um mesmo entregador por proximidade imediata.
     */
    public double distanciaAoPedido(PontoMapa posicaoEntregador, Pedido pedido) {
        if (pedido.getEnderecoEntrega() == null) return Double.MAX_VALUE;
        return rotaService.calcularDistanciaKm(posicaoEntregador, pedido.getEnderecoEntrega());
    }
}
