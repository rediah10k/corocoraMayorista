package com.organizacion.dto;

import lombok.*;

import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PedidoItemsRequest {
    private Long pedidoId;
    private List<ProductoSolicitado> productosSolicitados;
}
