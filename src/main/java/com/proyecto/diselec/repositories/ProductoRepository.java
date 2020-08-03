package com.proyecto.diselec.repositories;

import com.proyecto.diselec.models.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ProductoRepository  extends JpaRepository<Producto, Long> {

    List<Producto> findAllByCategoria(String categoria);

    List<Producto> findAllByPrecioVentaBetweenAndCategoriaContains(double precioMinino, double PrecioMaximo,
                                                                   String categoria);

    List<Producto> findAllByFechaIsBeforeOrderByFechaDesc(Date fechaActual);

    Producto findByNombre(String nombre);

    @Query("select p from Producto p where p.nombre like concat('%', :nombre,'%')")
    List<Producto> findAllByNombre(@Param("nombre") String nombre);

    @Query("select p from Producto p where p.nombre like concat('%', ?1,'%') and p.categoria = ?2")
    List<Producto> findByNombreAndCategoria(String nombre, String categoria);

    @Query("select p from Producto p where p.nombre like concat('%', ?1,'%') or p.categoria = ?2")
    List<Producto> findByNombreOrCategoria(String nombre, String categoria);

    @Query("select p from Producto p where p.nombre like concat('%', ?1,'%') or p.categoria = ?2 and p.cantidad = ?3")
    List<Producto> findByNombreOrCategoriaAndCantidad(String nombre, String categoria, int cantidad);
}
