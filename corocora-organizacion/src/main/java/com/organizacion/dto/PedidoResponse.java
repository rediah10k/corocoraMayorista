package com.organizacion.dto;

import com.organizacion.enitity.Cliente;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;



@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PedidoResponse {

    private Long pedidoId;

    private String estado;
}
