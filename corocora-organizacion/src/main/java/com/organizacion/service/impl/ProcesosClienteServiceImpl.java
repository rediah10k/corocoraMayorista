package com.organizacion.service.impl;

import com.organizacion.enitity.Cliente;
import com.organizacion.enitity.ProcesoCliente;
import com.organizacion.repository.ProcesoClienteRepository;
import com.organizacion.service.ProcesosClienteService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ProcesosClienteServiceImpl implements ProcesosClienteService {


    private final ProcesoClienteRepository procesoClienteRepository;

    @Override
    public void guardarProcesoCliente(String processId, Cliente cliente) {
        procesoClienteRepository.save(new ProcesoCliente(processId, cliente));
    }


}
