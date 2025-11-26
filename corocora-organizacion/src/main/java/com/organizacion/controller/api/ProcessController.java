package com.organizacion.controller.api;


import com.organizacion.enitity.Cliente;
import com.organizacion.enitity.ProcesoCliente;
import com.organizacion.service.ProcesosClienteService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("api/procesos")
public class ProcessController {

    private final ProcesosClienteService procesosClienteService;


    @PostMapping("crear")
    public ResponseEntity<?> crear(@RequestBody String processId, @AuthenticationPrincipal Cliente cliente) {

        procesosClienteService.guardarProcesoCliente(processId, cliente);

        return ResponseEntity.ok().build();

    }


}
