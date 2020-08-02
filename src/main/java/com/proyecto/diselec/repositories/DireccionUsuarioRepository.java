package com.proyecto.diselec.repositories;

import com.proyecto.diselec.models.DireccionUsuario;
import com.proyecto.diselec.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DireccionUsuarioRepository extends JpaRepository<DireccionUsuario, Long> {

    DireccionUsuario findByUsuario(Usuario usuario);

}
