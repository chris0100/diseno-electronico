package com.proyecto.diselec.services;

import com.proyecto.diselec.models.DireccionUsuario;
import com.proyecto.diselec.models.Usuario;
import com.proyecto.diselec.repositories.DireccionUsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DireccionUsuarioServiceImpl implements DireccionUsuarioService {

    @Autowired
    private DireccionUsuarioRepository direccionUsuarioRepositoryObj;

    @Override
    @Transactional(readOnly = true)
    public DireccionUsuario findDireccionUsuario(Usuario usuario) {
        return direccionUsuarioRepositoryObj.findByUsuario(usuario);
    }

    @Override
    @Transactional
    public void saveDireccionUsuario(DireccionUsuario direccionUsuario) {
        direccionUsuarioRepositoryObj.save(direccionUsuario);
    }
}
