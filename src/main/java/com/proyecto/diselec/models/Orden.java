package com.proyecto.diselec.models;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "orden")
public class Orden implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Date fechaRecepcionOrden;

    private Date fechaEntregaProducto;

    private String metodoEnvio;

    private double GrandTotal;


    @ManyToOne
    private Usuario usuario;

    @OneToMany(mappedBy = "orden")
    private List<ItemCarrito> listaItemCarrito;

    @OneToOne
    private DireccionUsuario direccionUsuario;

    @OneToOne
    private MetodoPago metodoPago;



    public Orden() {
    listaItemCarrito = new ArrayList<>();
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getFechaRecepcionOrden() {
        return fechaRecepcionOrden;
    }

    public void setFechaRecepcionOrden(Date fechaRecepcionOrden) {
        this.fechaRecepcionOrden = fechaRecepcionOrden;
    }

    public Date getFechaEntregaProducto() {
        return fechaEntregaProducto;
    }

    public void setFechaEntregaProducto(Date fechaEntregaProducto) {
        this.fechaEntregaProducto = fechaEntregaProducto;
    }

    public String getMetodoEnvio() {
        return metodoEnvio;
    }

    public void setMetodoEnvio(String metodoEnvio) {
        this.metodoEnvio = metodoEnvio;
    }

    public double getGrandTotal() {
        return GrandTotal;
    }

    public void setGrandTotal(double grandTotal) {
        GrandTotal = grandTotal;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public List<ItemCarrito> getListaItemCarrito() {
        return listaItemCarrito;
    }

    public void setListaItemCarrito(List<ItemCarrito> listaItemCarrito) {
        this.listaItemCarrito = listaItemCarrito;
    }


    public DireccionUsuario getDireccionUsuario() {
        return direccionUsuario;
    }

    public void setDireccionUsuario(DireccionUsuario direccionUsuario) {
        this.direccionUsuario = direccionUsuario;
    }

    public MetodoPago getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(MetodoPago metodoPago) {
        this.metodoPago = metodoPago;
    }

    private static final long serialVersionUID = 1L;
}
