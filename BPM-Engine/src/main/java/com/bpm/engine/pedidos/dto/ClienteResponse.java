package com.bpm.engine.pedidos.dto;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClienteResponse {

    private Long id;
    private String nombres;
    private Long documento;
    private Long saldo;
}
