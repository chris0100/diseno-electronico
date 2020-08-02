package com.proyecto.diselec.models;

import javax.persistence.*;
import java.util.Calendar;
import java.util.Date;

@Entity
public class PasswordResetToken {

    public static final int EXPIRATION = 60 *24;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;

    @OneToOne(targetEntity = Usuario.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private Usuario usuario;

    private Date fechaExpiracion;

    private String asuntoToken;

    public PasswordResetToken() {
    }

    public PasswordResetToken(final String token, final Usuario usuario, final String asuntoToken){
        super();
        this.token = token;
        this.usuario = usuario;
        this.asuntoToken = asuntoToken;
        this.fechaExpiracion = calcularFechaExpiracion(EXPIRATION);
    }

    public Date calcularFechaExpiracion(final int expiracionEnMinutos){
        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(new Date().getTime());
        cal.add(Calendar.MINUTE, expiracionEnMinutos);
        return new Date(cal.getTime().getTime());
    }

    public void actualizarToken(final String token){
        this.token = token;
        this.fechaExpiracion = calcularFechaExpiracion(EXPIRATION);
    }


    //GETTERS AND SETTERS
    public String getAsuntoToken() {
        return asuntoToken;
    }

    public void setAsuntoToken(String asuntoToken) {
        this.asuntoToken = asuntoToken;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Date getFechaExpiracion() {
        return fechaExpiracion;
    }

    public void setFechaExpiracion(Date fechaExpiracion) {
        this.fechaExpiracion = fechaExpiracion;
    }

    public static int getEXPIRATION() {
        return EXPIRATION;
    }
}

