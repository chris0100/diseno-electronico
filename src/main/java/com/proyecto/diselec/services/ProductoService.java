package com.proyecto.diselec.services;

import com.proyecto.diselec.models.Producto;

import java.util.Date;
import java.util.List;

public interface ProductoService {

    List<Producto> findAll();

    Producto findOne(Long id);

    void saveProducto(Producto producto);

    List<Producto> findAllByCategoria(String Categoria);

    List<Producto> findAllByPrecioVentaBetweenAndCategoria(double precioMinimo, double precioMaximo, String categoria);

    List<Producto> findAllByFechaIsBefore(Date fechaActual);

    Producto findByNombre(String nombre);

    List<Producto> findAllByName(String nombreProducto);

    List<Producto> findByNameAndCategoria(String nombreProducto, String categoria);

    List<Producto> findByNameOrCategoria(String nombreProducto, String categoria);

}
