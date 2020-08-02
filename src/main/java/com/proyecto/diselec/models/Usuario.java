package com.proyecto.diselec.models;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name="users")
public class Usuario implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, unique = true)
    private String username;

    @Column(length = 50)
    private String nombre;

    @Column(length = 60)
    private String password;

    private Boolean enabled;

    private Date fechaInscripcion;

    private String imagen;

    private Date fechaUltimaConexion;

    private String telefono;

    private String descripcion;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "usuario")
    private DireccionUsuario direccionUsuario;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "usuario")
    private List<MetodoPago> listaMetodosPago;

    @OneToOne(mappedBy = "usuario")
    private CarritoCompras carritoCompras;

    @OneToMany(mappedBy = "usuario")
    private List<Orden> listaOrdenes;





    //Un usuario puede tener muchos roles
    //LAZY: Cuando se cargan de forma “perezosa”, es decir, se cargan en el momento en que se requieren.
    //EAGER: Cuando se cargan de forma “proactiva”, es decir, al momento de cargar la entidad “owner” de la relación.
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private List<Role> listaRoles;




    public Usuario() {
        this.imagen = "loginImage.png";

    }

    // GETTERS AND SETTERS
    public DireccionUsuario getDireccionUsuario() {
        return direccionUsuario;
    }

    public void setDireccionUsuario(DireccionUsuario direccionUsuario) {
        this.direccionUsuario = direccionUsuario;
    }

    public List<MetodoPago> getListaMetodosPago() {
        return listaMetodosPago;
    }

    public void setListaMetodosPago(List<MetodoPago> listaMetodosPago) {
        this.listaMetodosPago = listaMetodosPago;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Date getFechaUltimaConexion() {
        return fechaUltimaConexion;
    }

    public void setFechaUltimaConexion(Date fechaUltimaConexion) {
        this.fechaUltimaConexion = fechaUltimaConexion;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public Date getFechaInscripcion() {
        return fechaInscripcion;
    }

    public void setFechaInscripcion(Date fechaInscripcion) {
        this.fechaInscripcion = fechaInscripcion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public List<Role> getListaRoles() {
        return listaRoles;
    }

    public void setListaRoles(List<Role> listaRoles) {
        this.listaRoles = listaRoles;
    }


    public CarritoCompras getCarritoCompras() {
        return carritoCompras;
    }

    public void setCarritoCompras(CarritoCompras carritoCompras) {
        this.carritoCompras = carritoCompras;
    }

    public List<Orden> getListaOrdenes() {
        return listaOrdenes;
    }

    public void setListaOrdenes(List<Orden> listaOrdenes) {
        this.listaOrdenes = listaOrdenes;
    }



    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", nombre='" + nombre + '\'' +
                ", password='" + password + '\'' +
                ", enabled=" + enabled +
                ", fechaInscripcion=" + fechaInscripcion +
                ", imagen='" + imagen + '\'' +
                ", fechaUltimaConexion=" + fechaUltimaConexion +
                ", telefono='" + telefono + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", direccionUsuario=" + direccionUsuario +
                ", listaMetodosPago=" + listaMetodosPago +
                ", carritoCompras=" + carritoCompras +
                ", listaRoles=" + listaRoles +
                '}';
    }

    private static final long serialVersionUID = 1L;
}
