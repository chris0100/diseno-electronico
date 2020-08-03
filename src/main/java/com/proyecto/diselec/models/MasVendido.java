package com.proyecto.diselec.models;

import java.math.BigDecimal;
import java.math.BigInteger;

public class MasVendido {

    private BigDecimal maximo;
    private BigInteger productoId;

    public MasVendido() {
    }


    public BigDecimal getMaximo() {
        return maximo;
    }

    public void setMaximo(BigDecimal maximo) {
        this.maximo = maximo;
    }

    public BigInteger getProductoId() {
        return productoId;
    }

    public void setProductoId(BigInteger productoId) {
        this.productoId = productoId;
    }

    @Override
    public String toString() {
        return "MasVendido{" +
                "maximo=" + maximo +
                ", productoId=" + productoId +
                '}';
    }
}
