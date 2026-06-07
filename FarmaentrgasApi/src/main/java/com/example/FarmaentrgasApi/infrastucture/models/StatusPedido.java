package com.example.FarmaentrgasApi.infrastucture.models;

public enum StatusPedido {
    AGUARDANDO,       // Pedido criado, aguardando entregador aceitar
    EM_SEPARACAO,     // Entregador aceitou, farmácia está separando
    SAIU_ENTREGA,     // Entregador saiu com os produtos
    ENTREGUE,         // Entrega confirmada
    CANCELADO         // Cancelado pelo cliente ou sistema
}

