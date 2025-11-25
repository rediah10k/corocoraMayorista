package com.clientes.dto;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductoResponse {
    private String id;
    private String nombre;
    private Integer precio;

}
