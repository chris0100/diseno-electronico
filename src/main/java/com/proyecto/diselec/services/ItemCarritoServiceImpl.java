package com.proyecto.diselec.services;

import com.proyecto.diselec.models.*;
import com.proyecto.diselec.repositories.ItemCarritoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class ItemCarritoServiceImpl implements ItemCarritoService {

    @Autowired
    ItemCarritoRepository itemCarritoRepositoryObj;

    @Override
    public void save(ItemCarrito itemCarrito) {
        itemCarritoRepositoryObj.save(itemCarrito);
    }

    @Override
    @Transactional(readOnly = true)
    public int findByProductoCount(Producto producto) {
        return itemCarritoRepositoryObj.findAllByProducto(producto).size();
    }


    @Override
    @Transactional(readOnly = true)
    public int listaCarrito(Producto producto, Usuario usuario) {
        try{
            int cantidad = itemCarritoRepositoryObj.findByProductoAndCarritoCompras_Usuario(producto, usuario).getCantidad();
            return cantidad;
        }
        catch (Exception e){
            return 0;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ItemCarrito findByProductoAndCarritoCompras(Producto producto, CarritoCompras carritoCompras) {
        return itemCarritoRepositoryObj.findByProductoAndCarritoCompras(producto, carritoCompras);
    }


    @Override
    @Transactional
    public void deleteById(Long id) {
        itemCarritoRepositoryObj.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public ItemCarrito findOne(Long id) {
        return itemCarritoRepositoryObj.findById(id).get();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemCarrito> findByCarritoCompras(CarritoCompras carritoCompras) {
        return itemCarritoRepositoryObj.findByCarritoCompras(carritoCompras);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemCarrito> findAllByOrden(Orden orden) {
        return itemCarritoRepositoryObj.findAllByOrden(orden);
    }

    @Override
    public List<ItemCarrito> findAll() {
        return itemCarritoRepositoryObj.findAll();
    }

    @Override
    public List<ItemCarrito> findAllByDate(Date fechaInicio, Date fechaFin) {
        return itemCarritoRepositoryObj.findAllByDate(fechaInicio,fechaFin);
    }


}
