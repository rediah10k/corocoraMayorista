package com.organizacion.repository;


import com.organizacion.enitity.Cliente;
import com.organizacion.enitity.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByCliente(Cliente cliente);
    List<Pedido> findByEstado(String estado);
    List<Pedido> findByClienteId(Long clienteId);
}

