package com.organizacion.dto;

import lombok.*;

import java.time.LocalDate;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PedidoFechaEnvioRequest {
    private Long pedidoId;
    private LocalDate fechaEnvio;
}
