package com.organizacion.service.impl;

import com.organizacion.dto.ClienteRequest;
import com.organizacion.dto.ClienteResponse;
import com.organizacion.enitity.Cliente;
import com.organizacion.repository.ClienteRepository;
import com.organizacion.service.ClienteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
@Slf4j
@RequiredArgsConstructor
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository clienteRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public ClienteResponse findByDocumento(Long documento) {
        Cliente cliente = clienteRepository.findByDocumento(documento).orElse(null);

        if(cliente != null){
            return ClienteResponse.builder()
                    .id(cliente.getId())
                    .numeroDocumento(cliente.getDocumento())
                    .nombres(cliente.getNombres())
                    .saldo(cliente.getSaldo())
                    .build();
        }

        return null;
    }

    @Override
    public ClienteResponse findByEmail(String email) {
        Cliente cliente = clienteRepository.findByEmail(email).orElse(null);

        if(cliente != null){
            return ClienteResponse.builder()
                    .id(cliente.getId())
                    .numeroDocumento(cliente.getDocumento())
                    .nombres(cliente.getNombres())
                    .saldo(cliente.getSaldo())
                    .build();
        }

        return null;
    }

    @Override
    public void crearUsuario(ClienteRequest clienteRequest) {

        clienteRepository.findByEmail(clienteRequest.getEmail()).ifPresent(
                existingUser -> {
                    throw new RuntimeException("Error al crear usuario: el usuario con el correo " +
                            existingUser.getEmail() + " ya está en uso");
                }
        );

        clienteRepository.save(
                Cliente.builder()
                        .nombres(clienteRequest.getNombres())
                        .documento(clienteRequest.getDocumento())
                        .email(clienteRequest.getEmail())
                        .password(passwordEncoder.encode(clienteRequest.getPassword()))
                        .saldo(0L)
                        .build()
        );


    }
}
