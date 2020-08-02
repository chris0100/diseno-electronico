package com.proyecto.diselec.models;

import javax.persistence.*;


@Entity
@Table(name = "metodosPago")
public class MetodoPago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tipo;

    private String nombreTitular;

    private String numeroTarjeta;

    private String cvc;

    private String mesExpiracion;

    private String yearExpiracion;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;




    //GETTERS AND SETTERS
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getNombreTitular() {
        return nombreTitular;
    }

    public void setNombreTitular(String nombreTitular) {
        this.nombreTitular = nombreTitular;
    }

    public String getNumeroTarjeta() {
        return numeroTarjeta;
    }

    public void setNumeroTarjeta(String numeroTarjeta) {
        this.numeroTarjeta = numeroTarjeta;
    }

    public String getCvc() {
        return cvc;
    }

    public void setCvc(String cvc) {
        this.cvc = cvc;
    }

    public String getMesExpiracion() {
        return mesExpiracion;
    }

    public void setMesExpiracion(String mesExpiracion) {
        this.mesExpiracion = mesExpiracion;
    }

    public String getYearExpiracion() {
        return yearExpiracion;
    }

    public void setYearExpiracion(String yearExpiracion) {
        this.yearExpiracion = yearExpiracion;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    @Override
    public String toString() {
        return "MetodoPago{" +
                "id=" + id +
                ", tipo='" + tipo + '\'' +
                ", nombreTitular='" + nombreTitular + '\'' +
                ", numeroTarjeta='" + numeroTarjeta + '\'' +
                ", cvc='" + cvc + '\'' +
                ", mesExpiracion='" + mesExpiracion + '\'' +
                ", yearExpiracion='" + yearExpiracion + '\'' +
                ", usuario=" + usuario +
                '}';
    }
}
