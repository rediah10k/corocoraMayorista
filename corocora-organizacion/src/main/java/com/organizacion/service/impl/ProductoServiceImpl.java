package com.organizacion.service.impl;

import com.organizacion.dto.ProductoResponse;
import com.organizacion.enitity.Producto;
import com.organizacion.repository.ProductoRepository;
import com.organizacion.service.ProductoService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;

    @Override
    public List<ProductoResponse> listarProductos() {
        List<Producto> productos = productoRepository.findAll();

        if(productos.isEmpty())
            return new ArrayList<>();

        return productos.stream()
                .map(producto -> ProductoResponse.builder()
                        .id(producto.getId())
                        .nombre(producto.getNombre())
                        .precio(producto.getPrecio())
                        .build()).toList();
    }
}
