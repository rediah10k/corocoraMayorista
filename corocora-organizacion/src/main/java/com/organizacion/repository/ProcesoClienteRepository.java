package com.organizacion.repository;

import com.organizacion.enitity.ProcesoCliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProcesoClienteRepository extends JpaRepository<ProcesoCliente, String> {
}
