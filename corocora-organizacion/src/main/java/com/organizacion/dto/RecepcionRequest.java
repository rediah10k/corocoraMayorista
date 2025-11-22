package com.organizacion.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RecepcionRequest {
    private String lote;
    private String producto;
    private Integer cantidad;
    private String fechaVencimiento;
}
