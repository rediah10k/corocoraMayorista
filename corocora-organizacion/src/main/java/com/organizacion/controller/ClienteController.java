package com.organizacion.controller;


import com.organizacion.dto.ClienteResponse;
import com.organizacion.enitity.Cliente;
import com.organizacion.service.ClienteService;
import com.organizacion.service.RecepcionService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("api/clientes")
public class ClienteController {

    private ClienteService clienteService;

    @GetMapping("/{documento}")
    private ResponseEntity<ClienteResponse> buscarPorDocumento(@PathVariable Long documento) {
        return ResponseEntity.ok(clienteService.findByDocumento(documento));
    }
}
