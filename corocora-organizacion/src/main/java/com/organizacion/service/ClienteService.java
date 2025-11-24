package com.organizacion.service;

import com.organizacion.dto.ClienteRequest;
import com.organizacion.dto.ClienteResponse;
import com.organizacion.enitity.Cliente;

import java.util.Optional;

public interface ClienteService {

    ClienteResponse findByDocumento(Long documento);

    ClienteResponse findByEmail(String email);

    void crearUsuario(ClienteRequest clienteRequest);

}
