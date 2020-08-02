package com.proyecto.diselec;

import com.proyecto.diselec.models.*;
import com.proyecto.diselec.repositories.DireccionUsuarioRepository;
import com.proyecto.diselec.repositories.MetodoPagoRepository;
import com.proyecto.diselec.repositories.ProductoRepository;
import com.proyecto.diselec.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SpringBootApplication
public class DisenoElectronicoApplication implements CommandLineRunner {


    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private UsuarioRepository usuarioRepositoryObj;

    @Autowired
    private ProductoRepository productoRepositoryObj;

    @Autowired
    private MetodoPagoRepository metodoPagoRepositoryObj;

    @Autowired
    private DireccionUsuarioRepository direccionUsuarioRepositoryObj;



    public static void main(String[] args) {
        SpringApplication.run(DisenoElectronicoApplication.class, args);
    }


    @Override
    public void run(String... args) throws Exception {

        //Estandar
        Usuario user = new Usuario();

        List<Role> listaRolesEstandar = new ArrayList<>();
        Role roleObj0 = new Role();
        roleObj0.setId(1L);
        roleObj0.setAuthority("ROLE_USER");

        listaRolesEstandar.add(roleObj0);

        user.setId(1L);
        user.setEnabled(true);
        user.setPassword(passwordEncoder.encode("12"));
        user.setUsername("user@gmail.com");
        user.setNombre("user");
        user.setFechaInscripcion(new Date());
        user.setListaRoles(listaRolesEstandar);
        usuarioRepositoryObj.save(user);


        //Admin
        Usuario userA = new Usuario();

        List<Role> listaRoles = new ArrayList<>();
        Role roleObj = new Role();
        roleObj.setId(2L);
        roleObj.setAuthority("ROLE_ADMIN");

        //Role roleObj2 = new Role();
        //roleObj2.setId(3L);
        //roleObj2.setAuthority("ROLE_USER");


        listaRoles.add(roleObj);
        //listaRoles.add(roleObj2);

        userA.setId(2L);
        userA.setEnabled(true);
        userA.setPassword(passwordEncoder.encode("12"));
        userA.setUsername("alexroguez59@gmail.com");
        userA.setNombre("admin");
        userA.setListaRoles(listaRoles);
        userA.setFechaInscripcion(new Date());
        usuarioRepositoryObj.save(userA);


        // Creacion de un producto ej
        for (int i = 0; i < 12; i++) {
            Producto producto = new Producto();
            producto.setId(1L + i);
            producto.setCantidad(2);
            producto.setCodigo("0010");
            producto.setDescripcion("Es un producto importado con unas caracteristicas muy importantes para la industria");
            producto.setEnabled(true);
            producto.setNombre("Transistor de alto Voltaje");
            producto.setPrecioCompra(2500);
            producto.setPrecioVenta(3500);
            producto.setUtilidad(producto.getPrecioVenta() - producto.getPrecioCompra());
            producto.setCategoria("Impresoras termicas");
            producto.setFecha(new Date());
            productoRepositoryObj.save(producto);
            i++;
        }



        //Nueva tarjeta de credito ej.
        MetodoPago metodoPago = new MetodoPago();
        metodoPago.setId(1L);
        metodoPago.setCvc("460");
        metodoPago.setTipo("MasterCard");
        metodoPago.setNombreTitular("Christian Rodriguez");
        metodoPago.setNumeroTarjeta("5143328870526045");
        metodoPago.setMesExpiracion("Junio");
        metodoPago.setYearExpiracion("2022");
        metodoPago.setUsuario(user);
        metodoPagoRepositoryObj.save(metodoPago);


        //Creacion de direccion de usuario
        DireccionUsuario direccionUsuario = new DireccionUsuario();
        direccionUsuario.setId(1L);
        direccionUsuario.setUsuario(user);
        direccionUsuario.setTelefono("3114045322");
        direccionUsuario.setCiudad("Cali");
        direccionUsuario.setDepartamento("Valle del Cauca");
        direccionUsuario.setCodigoPostal("76001");
        direccionUsuario.setNombre("Christian Rodriguez");
        direccionUsuario.setDireccion("Calle 44 #41-52");
        direccionUsuarioRepositoryObj.save(direccionUsuario);
    }

}

