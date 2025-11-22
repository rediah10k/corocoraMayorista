package com.organizacion.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecepcionRepository extends JpaRepository<com.organizacion.enitity.Recepcion, Long> {
}