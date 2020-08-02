package com.proyecto.diselec.services;

import com.proyecto.diselec.models.DireccionUsuario;
import com.proyecto.diselec.models.Usuario;

public interface DireccionUsuarioService {

    DireccionUsuario findDireccionUsuario(Usuario usuario);

    void saveDireccionUsuario(DireccionUsuario direccionUsuario);


}
