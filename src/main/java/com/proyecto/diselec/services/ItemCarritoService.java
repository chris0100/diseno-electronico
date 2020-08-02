package com.proyecto.diselec.services;

import com.proyecto.diselec.models.*;

import java.util.Date;
import java.util.List;

public interface ItemCarritoService {

    void save(ItemCarrito itemCarrito);

    int findByProductoCount(Producto producto);

    int listaCarrito(Producto producto, Usuario usuario);

    void deleteById(Long id);

    ItemCarrito findOne(Long id);

    ItemCarrito findByProductoAndCarritoCompras(Producto producto, CarritoCompras carritoCompras);

    List<ItemCarrito> findByCarritoCompras(CarritoCompras carritoCompras);

    List<ItemCarrito> findAllByOrden(Orden orden);

    List<ItemCarrito> findAll();

    List<ItemCarrito> findAllByDate(Date fechaInicio, Date fechaFin);




}
