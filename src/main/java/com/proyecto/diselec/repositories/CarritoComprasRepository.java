package com.proyecto.diselec.repositories;

import com.proyecto.diselec.models.CarritoCompras;
import com.proyecto.diselec.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarritoComprasRepository extends JpaRepository<CarritoCompras,Long> {


    CarritoCompras findByUsuario(Usuario usuario);

}
