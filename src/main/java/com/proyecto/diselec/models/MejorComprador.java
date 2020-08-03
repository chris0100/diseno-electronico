package com.proyecto.diselec.models;

import java.math.BigInteger;

public class MejorComprador {

    private BigInteger cantidadVentas;
    private BigInteger usuarioId;

    public MejorComprador() {
    }


    public BigInteger getCantidadVentas() {
        return cantidadVentas;
    }

    public void setCantidadVentas(BigInteger cantidadVentas) {
        this.cantidadVentas = cantidadVentas;
    }

    public BigInteger getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(BigInteger usuarioId) {
        this.usuarioId = usuarioId;
    }

    @Override
    public String toString() {
        return "MejorComprador{" +
                "cantidadVentas=" + cantidadVentas +
                ", usuarioId=" + usuarioId +
                '}';
    }
}
