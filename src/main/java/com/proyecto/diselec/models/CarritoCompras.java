package com.proyecto.diselec.models;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carritoCompras")
public class CarritoCompras {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double subTotal;

    @OneToOne
    private Usuario usuario;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "carritoCompras")
    private List<ItemCarrito> listaItemsCarrito;




    public CarritoCompras(){
        listaItemsCarrito = new ArrayList<>();
    }


    //GETTERS AND SETTERS
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(double subTotal) {
        this.subTotal = subTotal;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public List<ItemCarrito> getListaItemsCarrito() {
        return listaItemsCarrito;
    }

    public void setListaItemsCarrito(List<ItemCarrito> listaItemsCarrito) {
        this.listaItemsCarrito = listaItemsCarrito;
    }
}
