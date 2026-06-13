package com.example.FarmaentrgasApi.services;

import com.example.FarmaentrgasApi.controllers.dtos.*;
import com.example.FarmaentrgasApi.infrastucture.Repository.*;
import com.example.FarmaentrgasApi.infrastucture.models.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository       pedidoRepository;
    private final UsuarioRepository      usuarioRepository;
    private final FarmaciaRepository     farmaciaRepository;
    private final MedicamentoRepository  medicamentoRepository;
    private final EntregadorRepository   entregadorRepository;
    private final MedicamentoService     medicamentoService;
    private final EntregadorService      entregadorService;
    private final RotaService            rotaService;
    private final NotificacaoService     notificacaoService;
    private final GrafoRotaService       grafoRotaService;

    // ── Criar pedido (cliente finaliza compra no app) ─────────────────────────
    @Transactional
    public Pedido criar(CriarPedidoRequest request) {

        // 1. Busca entidades
        Usuario cliente = usuarioRepository.findById(request.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        Farmacia farmacia = farmaciaRepository.findById(request.getFarmaciaId())
                .orElseThrow(() -> new RuntimeException("Farmácia não encontrada"));

        // 2. Monta endereço de entrega
        //    Prioridade: (a) endereço explícito no request
        //                (b) localização actual do utilizador no mapa
        PontoMapa enderecoEntrega = new PontoMapa();

        if (request.getEnderecoEntrega() != null
                && request.getEnderecoEntrega().getLatitude() != null
                && request.getEnderecoEntrega().getLongitude() != null) {
            // Endereço fornecido explicitamente pelo app
            enderecoEntrega.setLatitude(request.getEnderecoEntrega().getLatitude());
            enderecoEntrega.setLongitude(request.getEnderecoEntrega().getLongitude());
            enderecoEntrega.setEnderecoFormatado(request.getEnderecoEntrega().getEnderecoFormatado());

        } else if (cliente.getPontoNoMapa() != null) {
            // Usa a localização actual do utilizador (atribuída no login)
            enderecoEntrega.setLatitude(cliente.getPontoNoMapa().getLatitude());
            enderecoEntrega.setLongitude(cliente.getPontoNoMapa().getLongitude());
            enderecoEntrega.setEnderecoFormatado(
                    cliente.getPontoNoMapa().getEnderecoFormatado() != null
                            ? cliente.getPontoNoMapa().getEnderecoFormatado()
                            : "Localização do utilizador: "
                                + cliente.getPontoNoMapa().getLatitude() + ", "
                                + cliente.getPontoNoMapa().getLongitude());
        } else {
            throw new RuntimeException(
                    "Endereço de entrega não fornecido e utilizador sem localização definida. " +
                    "Faça login novamente para obter uma localização automática.");
        }

        // 3. Cria o pedido base
        Pedido pedido = Pedido.builder()
                .cliente(cliente)
                .farmacia(farmacia)
                .status(StatusPedido.AGUARDANDO)
                .metodoPagamento(request.getMetodoPagamento())
                .enderecoEntrega(enderecoEntrega)
                .observacoes(request.getObservacoes())
                .criadoEm(LocalDateTime.now())
                .build();

        // 4. Monta os itens e calcula subtotal
        List<ItemPedido> itens    = new ArrayList<>();
        double           subtotal = 0.0;

        for (CriarPedidoRequest.ItemRequest itemReq : request.getItens()) {
            Medicamento med = medicamentoRepository.findById(itemReq.getMedicamentoId())
                    .orElseThrow(() -> new RuntimeException(
                            "Medicamento não encontrado: " + itemReq.getMedicamentoId()));

            if (!med.getDisponivel()) {
                throw new RuntimeException("Medicamento indisponível: " + med.getNome());
            }
            if (med.getEstoque() < itemReq.getQuantidade()) {
                throw new RuntimeException("Estoque insuficiente: " + med.getNome());
            }

            ItemPedido item = ItemPedido.builder()
                    .pedido(pedido)
                    .medicamento(med)
                    .quantidade(itemReq.getQuantidade())
                    .precoUnitario(med.getPreco())
                    .subtotal(med.getPreco() * itemReq.getQuantidade())
                    .build();

            itens.add(item);
            subtotal += item.getSubtotal();

            // Baixa estoque
            medicamentoService.baixarEstoque(med.getId(), itemReq.getQuantidade());
        }

        pedido.setItens(itens);
        pedido.setSubtotal(subtotal);

        // 5. Calcula taxa de entrega pela distância farmácia → cliente
        if (farmacia.getPontoNoMapa() != null) {
            RotaService.ResultadoRota rota = rotaService.calcularRota(
                    farmacia.getPontoNoMapa(), enderecoEntrega);
            pedido.setTaxaEntrega(rota.taxaEntrega());
            pedido.setDistanciaKm(rota.distanciaKm());
        } else {
            pedido.setTaxaEntrega(400.0); // taxa fixa padrão
        }

        pedido.setTotal(subtotal + pedido.getTaxaEntrega());

        // 6. Persiste o pedido
        Pedido salvo = pedidoRepository.save(pedido);

        // 7. Notifica entregadores disponíveis próximos à farmácia
        notificacaoService.notificarEntregadoresDisponiveis(salvo);

        return salvo;
    }

    // ── Buscar pedido por ID ──────────────────────────────────────────────────
    @Transactional(readOnly = true)
    public Pedido buscarPorId(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado: " + id));
    }

    // ── Histórico do cliente ──────────────────────────────────────────────────
    @Transactional(readOnly = true)
    public List<Pedido> listarPorCliente(Long clienteId) {
        return pedidoRepository.findByClienteIdOrderByCriadoEmDesc(clienteId);
    }

    // ── Pedidos da farmácia ───────────────────────────────────────────────────
    @Transactional(readOnly = true)
    public List<Pedido> listarPorFarmacia(Long farmaciaId) {
        return pedidoRepository.findByFarmaciaIdOrderByCriadoEmDesc(farmaciaId);
    }

    // ── Pedidos do entregador ─────────────────────────────────────────────────
    @Transactional(readOnly = true)
    public List<Pedido> listarPorEntregador(Long entregadorId) {
        return pedidoRepository.findByEntregadorIdOrderByCriadoEmDesc(entregadorId);
    }

    // ── Entregador aceita o pedido ────────────────────────────────────────────
    @Transactional
    public Pedido aceitarPedido(Long pedidoId, Long entregadorId) {
        Pedido     pedido     = buscarPorId(pedidoId);
        Entregador entregador = entregadorService.buscarPorId(entregadorId);

        if (pedido.getStatus() != StatusPedido.AGUARDANDO) {
            throw new RuntimeException("Pedido não está disponível para aceitar");
        }

        pedido.setEntregador(entregador);
        pedido.setStatus(StatusPedido.EM_SEPARACAO);
        pedido.setAtualizadoEm(LocalDateTime.now());

        // Entregador fica indisponível enquanto tem pedidos activos
        entregador.setDisponivel(false);
        entregadorRepository.save(entregador);

        return pedidoRepository.save(pedido);
    }

    // ── Atualiza status do pedido (farmácia / entregador) ────────────────────
    @Transactional
    public Pedido atualizarStatus(Long pedidoId, StatusPedido novoStatus) {
        Pedido pedido = buscarPorId(pedidoId);
        pedido.setStatus(novoStatus);
        pedido.setAtualizadoEm(LocalDateTime.now());

        // Quando entregue ou cancelado → libera o entregador se não tiver outros pedidos activos
        if (novoStatus == StatusPedido.ENTREGUE || novoStatus == StatusPedido.CANCELADO) {
            if (pedido.getEntregador() != null) {
                List<Pedido> ativos = pedidoRepository
                        .findPedidosAtivosDoEntregador(pedido.getEntregador().getId());
                boolean temOutros = ativos.stream()
                        .anyMatch(p -> !p.getId().equals(pedidoId));
                if (!temOutros) {
                    pedido.getEntregador().setDisponivel(true);
                    entregadorRepository.save(pedido.getEntregador());
                }
            }
        }

        return pedidoRepository.save(pedido);
    }

    // ── Cancelar pedido (pelo cliente) ────────────────────────────────────────
    @Transactional
    public Pedido cancelar(Long pedidoId) {
        Pedido pedido = buscarPorId(pedidoId);
        if (pedido.getStatus() == StatusPedido.SAIU_ENTREGA
                || pedido.getStatus() == StatusPedido.ENTREGUE) {
            throw new RuntimeException("Não é possível cancelar um pedido já em rota ou entregue");
        }
        return atualizarStatus(pedidoId, StatusPedido.CANCELADO);
    }

    // ── Avaliar pedido ────────────────────────────────────────────────────────
    @Transactional
    public Pedido avaliar(Long pedidoId, AvaliacaoRequest request) {
        Pedido pedido = buscarPorId(pedidoId);

        if (pedido.getStatus() != StatusPedido.ENTREGUE) {
            throw new RuntimeException("Só é possível avaliar pedidos entregues");
        }

        pedido.setNotaPedido(request.getNotaPedido());
        pedido.setNotaEntregador(request.getNotaEntregador());
        pedido.setComentarioAvaliacao(request.getComentario());

        if (request.getNotaEntregador() != null && pedido.getEntregador() != null) {
            entregadorService.atualizarAvaliacao(
                    pedido.getEntregador().getId(), request.getNotaEntregador());
        }

        return pedidoRepository.save(pedido);
    }

    // ── Pedidos aguardando entregador ─────────────────────────────────────────
    @Transactional(readOnly = true)
    public List<Pedido> listarAguardando() {
        return pedidoRepository.findByStatusOrderByCriadoEmAsc(StatusPedido.AGUARDANDO);
    }

    // ── Calcular melhor rota para um entregador com vários pedidos activos ────
    @Transactional(readOnly = true)
    public GrafoRotaService.ResultadoRota calcularRotaEntregador(Long entregadorId) {
        Entregador entregador = entregadorService.buscarPorId(entregadorId);

        if (entregador.getLocalizacaoAtual() == null) {
            throw new RuntimeException(
                    "Localização do entregador não disponível. " +
                    "Actualiza a localização antes de calcular a rota.");
        }

        List<Pedido> pedidosAtivos = pedidoRepository.findPedidosAtivosDoEntregador(entregadorId);

        return grafoRotaService.calcularMelhorRota(
                entregador.getLocalizacaoAtual(), pedidosAtivos);
    }
}
