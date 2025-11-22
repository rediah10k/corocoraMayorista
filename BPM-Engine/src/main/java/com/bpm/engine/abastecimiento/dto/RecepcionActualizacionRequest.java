package com.bpm.engine.abastecimiento.dto;

import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecepcionActualizacionRequest {
    Long recepcionId;
    String fechaIngreso;
    Integer prioridadSalida;
    String tipoRotacion;
    String ubicacionAlmacen;
}
