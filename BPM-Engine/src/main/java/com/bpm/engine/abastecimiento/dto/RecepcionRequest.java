package com.bpm.engine.abastecimiento.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecepcionRequest {
    private String lote;
    private String producto;
    private Integer cantidad;
    private String fechaVencimiento;
}
