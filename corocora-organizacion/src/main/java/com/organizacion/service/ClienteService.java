package com.organizacion.service;

import com.organizacion.dto.ClienteResponse;
import com.organizacion.enitity.Cliente;

import java.util.Optional;

public interface ClienteService {

    ClienteResponse findByDocumento(Long documento);

}
