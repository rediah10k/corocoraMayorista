package com.organizacion.controller;


import com.organizacion.dto.ClienteRequest;
import com.organizacion.dto.ClienteResponse;
import com.organizacion.enitity.Cliente;
import com.organizacion.service.ClienteService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("api/clientes/")
public class ClienteController {

    private ClienteService clienteService;


    @PostMapping("crear")
    ResponseEntity<Void> crearCliente(@RequestBody ClienteRequest clienteRequest){
        clienteService.crearUsuario(clienteRequest);
        return ResponseEntity.ok().build();
    }



    @GetMapping("info")
    ResponseEntity<ClienteResponse> infoCliente(@AuthenticationPrincipal Cliente cliente) {
        return ResponseEntity.ok(
                ClienteResponse.builder()
                        .id(cliente.getId())
                        .documento(cliente.getDocumento())
                        .nombres(cliente.getNombres())
                        .saldo(cliente.getSaldo())
                        .build()
        );
    }

    @GetMapping("{documento}")
    ResponseEntity<ClienteResponse> buscarPorDocumento(@PathVariable Long documento) {
        return ResponseEntity.ok(clienteService.findByDocumento(documento));
    }






}
