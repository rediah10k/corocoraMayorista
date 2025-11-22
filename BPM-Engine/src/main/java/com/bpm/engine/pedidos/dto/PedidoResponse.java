package com.bpm.engine.pedidos.dto;

import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PedidoResponse {

    private Long pedidoId;

    private String estado;
}
