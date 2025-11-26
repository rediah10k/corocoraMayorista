package com.organizacion.service;

import com.organizacion.enitity.Cliente;

public interface ProcesosClienteService {

    void guardarProcesoCliente(String processId, Cliente cliente);
}
