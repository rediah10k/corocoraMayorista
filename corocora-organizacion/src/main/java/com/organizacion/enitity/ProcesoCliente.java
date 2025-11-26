package com.organizacion.enitity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "procesos_clientes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProcesoCliente {

    @Id
    @Column(name = "proceso_id")
    private String processId;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

}
