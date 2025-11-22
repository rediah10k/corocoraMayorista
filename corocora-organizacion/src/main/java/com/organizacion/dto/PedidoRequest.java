package com.organizacion.dto;


import com.organizacion.enitity.Producto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PedidoRequest {
    private Long clienteId;
    private String estado;
}
