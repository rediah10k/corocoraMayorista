package com.organizacion.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RecepcionActualizacionRequest {
    Long recepcionId;
    String fechaIngreso;
    Integer prioridadSalida;
    String tipoRotacion;
    String ubicacionAlmacen;
}
