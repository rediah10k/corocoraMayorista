package com.bpm.engine.pedidos.dto;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PedidoRequest {
    private Long clienteId;
    private String estado;
}
