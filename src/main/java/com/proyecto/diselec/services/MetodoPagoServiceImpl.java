package com.proyecto.diselec.services;

import com.proyecto.diselec.models.MetodoPago;
import com.proyecto.diselec.models.Usuario;
import com.proyecto.diselec.repositories.MetodoPagoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MetodoPagoServiceImpl implements MetodoPagoService{

    @Autowired
    MetodoPagoRepository metodoPagoRepositoryObj;



    @Override
    @Transactional(readOnly = true)
    public List<MetodoPago> findAll() {
        return metodoPagoRepositoryObj.findAll();
    }


    @Override
    @Transactional(readOnly = true)
    public MetodoPago findById(Long id) {
        return metodoPagoRepositoryObj.findById(id).get();
    }

    @Override
    @Transactional(readOnly = true)
    public void save(MetodoPago metodoPago) {
        metodoPagoRepositoryObj.save(metodoPago);
    }

    @Override
    @Transactional
    public boolean deleteById(Long id) {
        try{
            metodoPagoRepositoryObj.deleteById(id);
            return true;
        }
        catch (Exception e){
            return false;
        }
    }


    @Override
    @Transactional(readOnly = true)
    public List<MetodoPago> findAllByUsuario(Usuario usuario) {
        return metodoPagoRepositoryObj.findAllByUsuario(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public MetodoPago findByNumeroTarjeta(String numeroTarjeta) {
        return metodoPagoRepositoryObj.findByNumeroTarjeta(numeroTarjeta);
    }
}
