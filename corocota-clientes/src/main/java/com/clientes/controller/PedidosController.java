package com.clientes.controller;


import com.clientes.config.UserSessionData;
import com.clientes.dto.ProductoResponse;
import com.clientes.service.CamundaService;
import com.clientes.service.ProductoService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class PedidosController {

    private final ObjectMapper objectMapper;
    private ProductoService productoService;
    private CamundaService camundaService;

    @Autowired
    private UserSessionData sessionData;

    @Value("${camunda.pedidos.process.name}")
    private String pedidosProcessName;

    public PedidosController(ObjectMapper objectMapper, ProductoService productoService, CamundaService camundaService) {
        this.objectMapper = objectMapper;
        this.productoService = productoService;
        this.camundaService = camundaService;
    }

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


        List<Map<String, Object>> listaProductos = new ArrayList<>();
        List<ProductoResponse> listaProductosBD = productoService.listarProductos();

        for(ProductoResponse productoResponse : listaProductosBD){
            Map<String, Object> producto = new HashMap<>();
            producto.put("id", productoResponse.getId());
            producto.put("nombre", productoResponse.getNombre());
            producto.put("precio", String.valueOf(productoResponse.getPrecio()));
            listaProductos.add(producto);
        }

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


        @PostMapping("/finalizar-pedido")
        public String finalizarPedido(@RequestParam("itemsCadena") String itemsCadena) {


            Map<String, Object> variables = new HashMap<>();

            Map<String, Object> documentoVar = new HashMap<>();
            documentoVar.put("value", sessionData.getClienteInfo().getNumeroDocumento());
            documentoVar.put("type", "Long");
            variables.put("numeroDocumento", documentoVar);


            Map<String, Object> productosVar = new HashMap<>();
            productosVar.put("value", itemsCadena);
            productosVar.put("type", "String");
            variables.put("productos", productosVar);


            String processId = camundaService.iniciarNuevoProceso(
                    pedidosProcessName,
                    variables

            );

            // 3. Redirecciona o retorna una vista de éxito
            return "pedido_exitoso"; // Nombre de tu plantilla de confirmación final
        }
}
