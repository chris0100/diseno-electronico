package com.proyecto.diselec.repositories;

import com.proyecto.diselec.models.MetodoPago;
import com.proyecto.diselec.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MetodoPagoRepository extends JpaRepository<MetodoPago, Long> {

    List<MetodoPago> findAllByUsuario(Usuario usuario);

    MetodoPago findByNumeroTarjeta(String numeroTarjeta);

}
