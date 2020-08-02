package com.proyecto.diselec.repositories;

import com.proyecto.diselec.models.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ProductoRepository  extends JpaRepository<Producto, Long> {

    List<Producto> findAllByCategoria(String categoria);

    List<Producto> findAllByPrecioVentaBetweenAndCategoriaContains(double precioMinino, double PrecioMaximo,
                                                                   String categoria);

    List<Producto> findAllByFechaIsBeforeOrderByFechaDesc(Date fechaActual);
}
