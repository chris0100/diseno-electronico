package com.proyecto.diselec.repositories;

import com.proyecto.diselec.models.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ItemCarritoRepository extends JpaRepository<ItemCarrito,Long> {
////////REVISAR
    List<ItemCarrito> findAllByProducto(Producto producto);

    ItemCarrito findByProductoAndCarritoCompras_Usuario(Producto producto, Usuario usuario);

    @Query("select sum(i.cantidad) from ItemCarrito i where i.producto=?1 and i.carritoCompras=?2")
    int findCantidad(Producto producto, CarritoCompras carritoCompras);

    @Query("select sum(i.subtotal) from ItemCarrito i where i.producto=?1 and i.carritoCompras=?2")
    double findSubTotal(Producto producto, CarritoCompras carritoCompras);

    ItemCarrito findByProductoAndCarritoCompras(Producto producto, CarritoCompras carritoCompras);

    List<ItemCarrito> findByCarritoCompras(CarritoCompras carritoCompras);

    List<ItemCarrito> findAllByOrden(Orden orden);

    @Query("select u from ItemCarrito u where u.orden.fechaRecepcionOrden between ?1 and ?2")
    List<ItemCarrito> findAllByDate(Date fechaInicio, Date fechaFin);



}
