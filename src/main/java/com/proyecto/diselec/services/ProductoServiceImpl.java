package com.proyecto.diselec.services;

import com.proyecto.diselec.models.Producto;
import com.proyecto.diselec.repositories.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class ProductoServiceImpl implements ProductoService {


    @Autowired
    ProductoRepository productoRepositoryObj;


    @Override
    @Transactional(readOnly = true)
    public List<Producto> findAll() {
        return productoRepositoryObj.findAll(Sort.by("nombre"));
    }

    @Override
    @Transactional(readOnly = true)
    public Producto findOne(Long id) {
        return productoRepositoryObj.findById(id).get();
    }

    @Override
    @Transactional
    public void saveProducto(Producto producto) {
        productoRepositoryObj.save(producto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Producto> findAllByCategoria(String categoria) {
        return productoRepositoryObj.findAllByCategoria(categoria);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Producto> findAllByPrecioVentaBetweenAndCategoria(double precioMinimo, double precioMaximo,
                                                                  String categoria) {
        return productoRepositoryObj.findAllByPrecioVentaBetweenAndCategoriaContains(precioMinimo,precioMaximo,categoria);
    }


    @Override
    public List<Producto> findAllByFechaIsBefore(Date fechaActual) {
        return productoRepositoryObj.findAllByFechaIsBeforeOrderByFechaDesc(fechaActual);
    }


    @Override
    public Producto findByNombre(String nombre) {
        return productoRepositoryObj.findByNombre(nombre);
    }


    @Override
    public List<Producto> findAllByName(String nombreProducto) {
        return productoRepositoryObj.findAllByNombre(nombreProducto);
    }


    @Override
    public List<Producto> findByNameAndCategoria(String nombreProducto, String categoria) {
        return productoRepositoryObj.findByNombreAndCategoria(nombreProducto,categoria);
    }

    @Override
    public List<Producto> findByNameOrCategoria(String nombreProducto, String categoria) {
        return productoRepositoryObj.findByNombreOrCategoria(nombreProducto,categoria);
    }
}
