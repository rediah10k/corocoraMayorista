package com.organizacion.service.impl;

import com.organizacion.dto.ClienteResponse;
import com.organizacion.enitity.Cliente;
import com.organizacion.repository.ClienteRepository;
import com.organizacion.service.ClienteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
@Slf4j
@RequiredArgsConstructor
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository clienteRepository;

    @Override
    public ClienteResponse findByDocumento(Long documento) {
        Cliente cliente = clienteRepository.findByDocumento(documento).orElse(null);

        if(cliente != null){
            return ClienteResponse.builder()
                    .id(cliente.getId())
                    .documento(cliente.getDocumento())
                    .nombres(cliente.getNombres())
                    .saldo(cliente.getSaldo())
                    .build();
        }

        return null;
    }
}
