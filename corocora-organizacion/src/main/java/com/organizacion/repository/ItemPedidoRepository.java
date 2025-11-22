package com.organizacion.repository;



import com.organizacion.enitity.ItemPedido;
import com.organizacion.enitity.Pedido;
import com.organizacion.enitity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemPedidoRepository extends JpaRepository<ItemPedido, Long> {
    List<ItemPedido> findByPedido(Pedido pedido);
    List<ItemPedido> findByProducto(Producto producto);
}

