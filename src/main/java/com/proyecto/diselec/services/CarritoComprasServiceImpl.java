package com.proyecto.diselec.services;

import com.proyecto.diselec.models.CarritoCompras;
import com.proyecto.diselec.models.ItemCarrito;
import com.proyecto.diselec.models.Usuario;
import com.proyecto.diselec.repositories.CarritoComprasRepository;
import com.proyecto.diselec.repositories.ItemCarritoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CarritoComprasServiceImpl implements CarritoComprasService {

    @Autowired
    CarritoComprasRepository carritoComprasRepositoryObj;

    @Autowired
    ItemCarritoRepository itemCarritoRepositoryObj;

    @Override
    @Transactional(readOnly = true)
    public CarritoCompras findByUsuario(Usuario usuario) {
        return carritoComprasRepositoryObj.findByUsuario(usuario);
    }

    @Override
    @Transactional
    public void save(CarritoCompras carritoCompras) {
        carritoComprasRepositoryObj.save(carritoCompras);
    }

    @Override
    @Transactional
    public void delete(CarritoCompras carritoCompras) {
        carritoComprasRepositoryObj.delete(carritoCompras);
    }

    @Override
    @Transactional
    public void limpiarCarrito(CarritoCompras carritoCompras) {
        //Buscamos la lista de ItemCarrito
        List<ItemCarrito> listaItemCarrito = itemCarritoRepositoryObj.findByCarritoCompras(carritoCompras);

        for (ItemCarrito obj : listaItemCarrito){
            obj.setCarritoCompras(null);
            itemCarritoRepositoryObj.save(obj);
        }

        //Se establece en cero el valor del carrito de compras
        carritoCompras.setSubTotal(0);
        carritoComprasRepositoryObj.save(carritoCompras);
    }
}
