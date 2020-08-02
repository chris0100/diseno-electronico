package com.proyecto.diselec.services;

import com.proyecto.diselec.models.CarritoCompras;
import com.proyecto.diselec.models.Usuario;

public interface CarritoComprasService {

    CarritoCompras findByUsuario(Usuario usuario);

    void save(CarritoCompras carritoCompras);

    void delete(CarritoCompras carritoCompras);

    void limpiarCarrito(CarritoCompras carritoCompras);

}
