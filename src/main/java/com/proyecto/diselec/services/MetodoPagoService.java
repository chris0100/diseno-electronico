package com.proyecto.diselec.services;


import com.proyecto.diselec.models.MetodoPago;
import com.proyecto.diselec.models.Usuario;

import java.util.List;

public interface MetodoPagoService {

    List<MetodoPago> findAll();

    List<MetodoPago> findAllByUsuario(Usuario usuario);

    MetodoPago findById(Long id);

    void save(MetodoPago metodoPago);

    boolean deleteById(Long id);

    MetodoPago findByNumeroTarjeta(String numeroTarjeta);


}
