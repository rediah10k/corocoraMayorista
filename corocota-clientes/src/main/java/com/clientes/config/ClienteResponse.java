package com.clientes.config;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClienteResponse {

    private Long id;
    private String nombres;
    private Long numeroDocumento;
    private Long saldo;
}
