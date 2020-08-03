package com.proyecto.diselec.services;

import com.proyecto.diselec.models.Orden;
import com.proyecto.diselec.models.Usuario;
import com.proyecto.diselec.repositories.OrdenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class OrdenServiceImpl implements OrdenService {

    @Autowired
    OrdenRepository ordenRepositoryObj;

    @Override
    @Transactional
    public void save(Orden orden) {
        ordenRepositoryObj.save(orden);
    }

    @Override
    @Transactional(readOnly = true)
    public Orden findOne(Long id) {
        return ordenRepositoryObj.findById(id).get();
    }


    @Override
    @Transactional(readOnly = true)
    public List<Orden> findAllByUsuario(Usuario usuario) {
        return ordenRepositoryObj.findByUsuario(usuario);
    }

    @Override
    public double findByFechas(Date fechaInicio, Date fechaFin) {
        try{
            return ordenRepositoryObj.findByDates(fechaInicio,fechaFin);
        }
        catch (Exception e){
            return 0;
        }
    }

}
