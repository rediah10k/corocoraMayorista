package com.organizacion.controller.api;


import com.organizacion.dto.RecepcionActualizacionRequest;
import com.organizacion.dto.RecepcionRequest;
import com.organizacion.service.RecepcionService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Logger;

@RestController
@AllArgsConstructor
@RequestMapping("api/inventario")
public class RecepcionRestController {

    private final Logger logger = Logger.getLogger(RecepcionRestController.class.getName());

    private RecepcionService recepcionService;

    @PostMapping("insertar")
    ResponseEntity<Long> insertarInventario(@RequestBody RecepcionRequest recepcionRequest){
        return ResponseEntity.ok(recepcionService.insertarRecepcion(recepcionRequest));
    }

    @PutMapping("actualizar")
    ResponseEntity<?> actualizarInventario(@RequestBody RecepcionActualizacionRequest recepcionActualizacionRequest){
        recepcionService.actualizarInventario(recepcionActualizacionRequest);
        return ResponseEntity.ok(null);
    }


}
