package com.organizacion.controller;

import com.organizacion.dto.*;
import com.organizacion.enitity.Cliente;
import com.organizacion.service.PedidosService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@AllArgsConstructor
@RequestMapping("api/pedidos")
public class PedidosController {

        private PedidosService pedidosService;


        @PostMapping("/proceso/iniciar")
        ResponseEntity<Void> iniciarPedido(@AuthenticationPrincipal Cliente cliente,@RequestBody String productosStr){
            String productosLimpios = productosStr.replace("\r", "");

            return ResponseEntity.ok().build();
        }

        @PostMapping("validar")
        ResponseEntity<Map<String, Object>> validarStockProductos(@RequestBody List<ProductoSolicitado> productoSolicitadoList){

            return ResponseEntity.ok(pedidosService.validarStockProductos(productoSolicitadoList));
        }

        @PostMapping("crear")
        ResponseEntity<PedidoResponse> crearPedido(@RequestBody PedidoRequest pedidoRequest){
            return ResponseEntity.ok(pedidosService.crearPedido(pedidoRequest));
        }


        @PostMapping("items")
        ResponseEntity<String> crearItemsPedido(@RequestBody PedidoItemsRequest pedidoItemsRequest){
            return ResponseEntity.ok(pedidosService.crearItemsPedido(pedidoItemsRequest));
        }


        @PutMapping("agendar")
        ResponseEntity<Void> agendarFechaEnvio(@RequestBody PedidoFechaEnvioRequest pedidoFechaEnvioRequest){
            pedidosService.agendarFechaEnvio(pedidoFechaEnvioRequest);
            return ResponseEntity.ok(null);
        }
}
