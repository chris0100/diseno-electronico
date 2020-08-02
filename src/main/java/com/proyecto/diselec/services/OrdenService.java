package com.proyecto.diselec.services;

import com.proyecto.diselec.models.Orden;
import com.proyecto.diselec.models.Usuario;

import java.util.Date;
import java.util.List;

public interface OrdenService {

    void save(Orden orden);

    Orden findOne(Long id);

    List<Orden> findAllByUsuario(Usuario usuario);

    double findByFechas(Date fechaInicio, Date fechaFin);

}
