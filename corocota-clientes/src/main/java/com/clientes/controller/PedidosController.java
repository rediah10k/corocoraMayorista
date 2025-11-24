package com.clientes.controller;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class PedidosController {

    private final ObjectMapper objectMapper = new ObjectMapper();


    @GetMapping("pedidos")
    public String pedidos(Model model){

        List<Map<String, Object>> listaPedidos = new ArrayList<>();
        listaPedidos.add(Map.of("id", "1001", "total", 350.50, "moneda", "USD", "estado", "PREPARANDO", "fecha", "2025-11-20"));
        listaPedidos.add(Map.of("id", "1002", "total", 120.00, "moneda", "USD", "estado", "ENVIADO", "fecha", "2025-11-18"));
        // ... agrega más pedidos

        // 2. Pasar la lista al modelo
        model.addAttribute("pedidos_lista", listaPedidos);
        return "pedidos";
    }



    @GetMapping("/crear-pedido")
    public String mostrarFormularioPedido(Model model) {

        // 1. Crear la lista de Mapas
        List<Map<String, Object>> listaProductos = new ArrayList<>();

        // 2. Crear Mapas (simulando filas de datos) y añadirlos a la lista

        // --- Producto 1 ---
        Map<String, Object> producto1 = new HashMap<>();
        producto1.put("id", 101L);
        producto1.put("nombre", "Cable UTP Cat 6");
        producto1.put("precio", "25.50");
        producto1.put("imagenUrl", "https://st4.depositphotos.com/11953928/24778/v/450/depositphotos_247781716-stock-illustration-supermarket-grocery-products-cartoon.jpg");
        listaProductos.add(producto1);

        // --- Producto 2 ---
        Map<String, Object> producto2 = new HashMap<>();
        producto2.put("id", 102L);
        producto2.put("nombre", "Router Gigabit 5GHz");
        producto2.put("precio", new BigDecimal("120.99"));
        producto2.put("imagenUrl", "https://st4.depositphotos.com/11953928/24778/v/450/depositphotos_247781716-stock-illustration-supermarket-grocery-products-cartoon.jpg");
        listaProductos.add(producto2);

        // --- Producto 3 ---
        Map<String, Object> producto3 = new HashMap<>();
        producto3.put("id", 103L);
        producto3.put("nombre", "Disco SSD 1TB");
        producto3.put("precio", "85.00");
        producto3.put("imagenUrl", "https://st4.depositphotos.com/11953928/24778/v/450/depositphotos_247781716-stock-illustration-supermarket-grocery-products-cartoon.jpg");
        listaProductos.add(producto3);

        // --- Producto 4 ---
        Map<String, Object> producto4 = new HashMap<>();
        producto4.put("id", 104L);
        producto4.put("nombre", "Memoria RAM 16GB");
        producto4.put("precio", "45.75");
        producto4.put("imagenUrl", "https://st4.depositphotos.com/11953928/24778/v/450/depositphotos_247781716-stock-illustration-supermarket-grocery-products-cartoon.jpg");
        listaProductos.add(producto4);

        // --- Producto 4 ---



        // --- Producto 5 ---
        Map<String, Object> producto5 = new HashMap<>();
        producto5.put("id", 105L);
        producto5.put("nombre", "Monitor");
        producto5.put("precio", "250.00");
        producto5.put("imagenUrl", "https://st4.depositphotos.com/11953928/24778/v/450/depositphotos_247781716-stock-illustration-supermarket-grocery-products-cartoon.jpg");
        listaProductos.add(producto5);


        // --- Producto 5 ---
        Map<String, Object> producto6 = new HashMap<>();
        producto6.put("id", 106L);
        producto6.put("nombre", "PRODUCTO 6");
        producto6.put("precio", "12.00");
        producto6.put("imagenUrl", "https://st4.depositphotos.com/11953928/24778/v/450/depositphotos_247781716-stock-illustration-supermarket-grocery-products-cartoon.jpg");
        listaProductos.add(producto6);





        // 3. Pasar la lista de Mapas al modelo
        model.addAttribute("listaProductos", listaProductos);

        // 4. Retornar la vista
        return "crear_pedido";
    }


    @PostMapping("/confirmar-compra")
    public String revisarPedido(@RequestParam("itemsJson") String itemsJson, Model model) {
        try {
            // 1. Convertir el String JSON a una Lista de Mapas (o tu clase DTO)
            List<Map<String, Object>> itemsCarrito = objectMapper.readValue(
                    itemsJson,
                    new TypeReference<List<Map<String, Object>>>() {}
            );

            // 2. Calcular totales en el servidor (Más seguro que confiar en el JS)
            double total = 0;
            for (Map<String, Object> item : itemsCarrito) {
                // Asegúrate de castear o convertir tipos correctamente según tu JSON
                double precio = Double.parseDouble(item.get("precio").toString());
                int cantidad = Integer.parseInt(item.get("cantidad").toString());
                double subtotal = precio * cantidad;
                item.put("subtotal", subtotal); // Añadimos subtotal al mapa
                total += subtotal;
            }

            // 3. Pasar los datos a la vista de confirmación
            model.addAttribute("items", itemsCarrito);
            model.addAttribute("totalPedido", total);

            return "confirmar_compra"; // Nombre de tu plantilla HTML de resumen

        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/crear-pedido?error=json";
        }
    }
}
