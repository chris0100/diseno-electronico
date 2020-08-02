package com.proyecto.diselec.controllers;

import com.proyecto.diselec.models.ItemCarrito;
import com.proyecto.diselec.models.MasVendido;
import com.proyecto.diselec.models.Producto;
import com.proyecto.diselec.models.Usuario;
import com.proyecto.diselec.services.*;
import com.proyecto.diselec.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.imageio.ImageIO;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/panel-admin")
public class AdminController {


    //Encripatdor para password
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private UsuarioService usuarioServiceObj;

    @PersistenceContext
    EntityManager em;

    @Autowired
    private ProductoService productoServiceObj;

    @Autowired
    private UploadFileService uploadFileServiceObj;

    @Autowired
    private OrdenService ordenServiceObj;

    @Autowired
    private ItemCarritoService itemCarritoServiceObj;


    // Panel de administrador
    @GetMapping("")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String panelAdmin() {
        return "panel-admin/panel-admin";
    }


    // Formulario para cambiar password
    @GetMapping("/cambiar-password-admin")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String cambiarPasswordAdmin() {
        return "panel-admin/cambiar-password-admin";
    }


    //Se envian los password(actual y nuevo) para cambio
    @PostMapping("/cambiar-password-admin")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String cambiarPaswwordAdminPost(@ModelAttribute("actualPassword") String actualPassword,
                                           @ModelAttribute("nuevoPassword") String nuevoPassword,
                                           RedirectAttributes redirectAttributes, Model model, Principal principal) {

        //Verifica que los campos de los password no esten vacios
        if (!actualPassword.trim().isEmpty() && !nuevoPassword.trim().isEmpty()) {
            //Trae los datos del usuario actualmente logueado
            Usuario usuario = usuarioServiceObj.findByUsername(principal.getName());

            if (usuario == null) {
                redirectAttributes.addFlashAttribute("warning", "Debes loguearte para efectuar cambios");
                return "redirect:/login";
            }

            //Verifica que el actual password coincida con el guardado en la base de datos
            String passwordActualEncriptado = usuario.getPassword();

            if (passwordEncoder.matches(actualPassword, passwordActualEncriptado)) {
                usuario.setPassword(passwordEncoder.encode(nuevoPassword));

                //Se guarda en la BD
                usuarioServiceObj.saveUsuario(usuario);

                //Redirecciona a el panel principal
                model.addAttribute("success", "Password modificado correctamente");
                return "panel-admin/panel-admin";

            } else {
                model.addAttribute("error", "El password actual no coincide con el almacenado en el sistema");
                return "panel-admin/cambiar-password-admin";
            }
        } else {
            model.addAttribute("error", "Todos los campos deben estar diligenciados");
            return "panel-admin/cambiar-password-admin";
        }
    }


    // Panel de lista de clientes
    @GetMapping("/clientes-admin")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String clientesAdmin(Model model) {

        //Se realiza query nativo para traer los usuarios con ROLE_USER
        Query nativeQuery = em.createNativeQuery("select u.id,nombre,username,enabled,fecha_inscripcion from users u inner join authorities a on u.id = a.user_id where a.authority = 'ROLE_USER'");
        List<Object[]> results = nativeQuery.getResultList();
        List<Usuario> listaUsuarios = results.stream().map(obj -> {
            Usuario usuario = new Usuario();
            usuario.setId(((BigInteger) obj[0]).longValue());
            usuario.setNombre((String) obj[1]);
            usuario.setUsername((String) obj[2]);
            usuario.setEnabled((Boolean) obj[3]);
            usuario.setFechaInscripcion((Date) obj[4]);
            return usuario;
        }).collect(Collectors.toList());

        if (listaUsuarios.size() == 0) {
            model.addAttribute("vacio", "No hay usuarios en la Base de datos");
            return "panel-admin/clientes-admin";
        }

        model.addAttribute("usuarios", listaUsuarios);
        return "panel-admin/clientes-admin";
    }


    //Modificar el estado del cliente
    @GetMapping("/clientes-admin/modificar-estado-cliente/{clienteId}/{tipo}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String modificarEstadoCliente(@PathVariable("clienteId") Long clienteId,
                                         RedirectAttributes redirectAttributes, @PathVariable("tipo") String tipo) {


        // Se busca el cliente en la base de datos
        Usuario usuario = usuarioServiceObj.findById(clienteId);

        //Si el cliente no aparece en la BD, envia mensaje
        if (usuario == null) {
            redirectAttributes.addFlashAttribute("error", "El cliente no se encuentra en la base de datos");
            return "redirect:/panel-admin/clientes-admin";
        }

        //realiza el cambio de estado
        if (tipo.equals("Inactivar")) {
            usuario.setEnabled(false);
        } else {
            usuario.setEnabled(true);
        }

        //guarda los cambios en la BD
        usuarioServiceObj.saveUsuario(usuario);

        redirectAttributes.addFlashAttribute("success", "Cliente modificado correctamente");
        return "redirect:/panel-admin/clientes-admin";
    }


    //*******************************************************************************************
    //*******************************MANEJO DE PRODUCTOS*****************************************
    //*******************************************************************************************

    // Panel de productos
    @GetMapping("/productos-admin")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String productosAdmin(Model model) {

        List<Producto> listaProductos = productoServiceObj.findAll();
        System.out.println(listaProductos.size());

        // Si la lista de productos esta vacia
        if (listaProductos.size() == 0) {
            model.addAttribute("vacio", "No hay productos en la Base de datos");
            return "panel-admin/productos-admin";
        }

        // Envia el atributo
        model.addAttribute("productos", listaProductos);

        return "panel-admin/productos-admin";
    }


    //Cambiar estado de producto
    @GetMapping("/productos-admin/modificar-estado-producto/{productoId}/{tipo}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String modificarProductoAdmin(@PathVariable("productoId") Long productoId,
                                         @PathVariable("tipo") String tipo, Model model,
                                         RedirectAttributes redirectAttributes) {

        //Localicar el producto en la BD
        Producto producto = productoServiceObj.findOne(productoId);

        if (producto == null) {
            redirectAttributes.addFlashAttribute("error", "El producto no se encuentra en el sistema");
            return "redirect:/panel-admin/productos-admin";
        }

        // Se cambia el estado al producto
        if (tipo.equals("Inactivar")) {
            producto.setEnabled(false);
        } else {
            producto.setEnabled(true);
        }

        // Se guarda el producto en la BD
        productoServiceObj.saveProducto(producto);

        redirectAttributes.addFlashAttribute("success", "Producto modificado correctamente");
        return "redirect:/panel-admin/productos-admin";
    }


    // Formulario para añadir producto
    @GetMapping("/productos-admin/form-producto")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String formProductosAdmin(@RequestParam(value = "productoId", required = false) Long productoId, Model model) {

        //Listado de categorias
        List<String> listadoCategorias = Util.listaCategorias();

        if (productoId != null) {
            //Se localiza el producto si es el caso para editar
            Producto producto = productoServiceObj.findOne(productoId);
            model.addAttribute("producto", producto);

            model.addAttribute("listaCategorias", listadoCategorias);
            return "panel-admin/form-producto-admin";
        }

        //Si no tiene un id, se tomara como para la creacion de un nuevo producto
        Producto producto = new Producto();
        model.addAttribute("producto", producto);
        model.addAttribute("listaCategorias", listadoCategorias);
        return "panel-admin/form-producto-admin";
    }


    //Carga de la imagen para dar funcionalidad, sin este no se muestra la imagen en pantalla
    @GetMapping("/uploads/{filename:.+}")
    public ResponseEntity<Resource> verFoto(@PathVariable String filename) {
        Resource recurso = null;

        try {
            recurso = uploadFileServiceObj.load(filename);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + recurso.getFilename() + "\"")
                .body(recurso);
    }


    //Guardar un producto
    @PostMapping("/productos-admin/guardar-producto")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String guardarProducto(@ModelAttribute("producto") Producto producto, RedirectAttributes redirectAttributes,
                                  Model model, @RequestParam("file") MultipartFile foto) {

        //Verifica que todos los campos esten diligenciados
        if (!producto.getCodigo().trim().isEmpty() && !producto.getNombre().trim().isEmpty() &&
                producto.getCantidad() > 0 && producto.getPrecioCompra() > 0 && producto.getPrecioVenta() > 0 &
                !producto.getDescripcion().trim().isEmpty() && !producto.getNombre().trim().isEmpty()) {

            // Si la foto no esta vacia
            if (!foto.isEmpty()) {

                //Verificar que si sea una imagen
                try {
                    InputStream input = foto.getInputStream();

                    try {
                        ImageIO.read(input).toString();
                    } catch (Exception e) {
                        model.addAttribute("error", "El archivo no es una imagen");
                        model.addAttribute("producto", producto);
                        return "panel-admin/form-producto-admin";
                    }
                } catch (Exception e) {
                    System.out.println("primero no es un archivo valido");
                }


                //Se elimina la foto anterior, ya que se esta enviando una nueva
                if (producto.getId() != null) {
                    uploadFileServiceObj.delete(producto.getImagen());
                    System.out.println("borro la foto anterior");
                }

                //Se carga la imagen
                String uniqueFilename = null;

                try {
                    uniqueFilename = uploadFileServiceObj.copy(foto);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                System.out.println("nombre de la nueva foto " + uniqueFilename);
                //Se agrega el nombre de la foto al producto
                producto.setImagen(uniqueFilename);
            }

            //Se calcula la utilidad
            producto.setUtilidad(producto.getPrecioVenta() - producto.getPrecioCompra());

            //Se guarda en la BD
            productoServiceObj.saveProducto(producto);

            String mensajeFlash = (producto.getId() != null) ? "Producto modificado satisfactoriamente" : "Producto creado satisfactoriamente";
            redirectAttributes.addFlashAttribute("success", mensajeFlash);

            return "redirect:/panel-admin/productos-admin";
        } else {
            model.addAttribute("error", "Revisa que los campos esten correctos");
            model.addAttribute("producto", producto);
            return "panel-admin/form-producto-admin";
        }
    }


    // Panel de informes
    @GetMapping("/informes-admin")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String informesAdmin() {
        return "panel-admin/informes-admin";
    }


    // Panel de informes
    @PostMapping("/informes-detalle-admin")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String informesDetalleAdmin(@ModelAttribute("fechaInicio") String fechaInit, @ModelAttribute("fechaFin") String fechaF,
                                       Model model, RedirectAttributes redirectAttributes) {
        System.out.println(fechaInit + ":" + fechaF);

        //Conversion del formato de fecha tomado de la seleccion
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
        Date fechaInicio = null;
        Date fechaFin = null;

        try {
            fechaInicio = formato.parse(fechaInit);
            fechaFin = formato.parse(fechaF);

            //Compara las fechas, para que la fechaFin sea mayor a la fecha Inicion
            if (fechaFin.before(fechaInicio)) {
                redirectAttributes.addFlashAttribute("error", "La Fecha Fin debe ser mayor a la Fecha Inicio");
                return "redirect:/panel-admin/informes-admin";
            }
        } catch (ParseException e) {
            System.out.println("error conversion fecha");
        }


        //Realiza consulta para calculo de fechas
        double totalVendido = ordenServiceObj.findByFechas(fechaInicio, fechaFin);

        System.out.println(totalVendido + " total vendido en rango");
        //Si no arroja resultados en total vendido en rango
        if (totalVendido <= 0.0){
            redirectAttributes.addFlashAttribute("warning", "No se han encontrado resultados en ese rango de fechas");
            return "redirect:/panel-admin/informes-admin";
        }


        //Calcula el producto que mas veces se ha vendido
        //Query nativeQuery = em.createNativeQuery("select max(cuenta),producto_id from (select producto_id, count(producto_id) cuenta from item_carrito group by producto_id) as T");

        //Calculo del producto con mas ventas en sus cantidades en el rango de fecha establecido
        Query nativeQuery = em.createNativeQuery("select cantidadProductosVendidos, producto_id from(select producto_id, sum(cantidad) cantidadProductosVendidos from item_carrito where orden_id in \n" +
                "(select id from orden where fecha_recepcion_orden between" + "'"+fechaInit+"'"+ "and '"+ fechaF+"') \n" +
                "group by producto_id) as T \n" +
                "where cantidadProductosVendidos = \n" +
                "(select max(cantidadProductosVendidos) from(select producto_id, sum(cantidad) cantidadProductosVendidos from item_carrito where orden_id in \n" +
                "(select id from orden where fecha_recepcion_orden between" + "'"+fechaInit+"'"+ "and '"+ fechaF+"') \n" +
                "group by producto_id) as F )");

        List<Object[]> results = nativeQuery.getResultList();

        List<MasVendido> resultados = results.stream().map(obj -> {
            MasVendido masVendido = new MasVendido();
            masVendido.setMaximo((BigDecimal) obj[0]);
            masVendido.setProductoId((BigInteger) obj[1]);
            return masVendido;
        }).collect(Collectors.toList());



        //Localizar a traves del id del producto
        Producto producto = productoServiceObj.findOne(resultados.get(0).getProductoId().longValue());


        //obtener la utilidad de todas las ventas en el rango de fecha
        List<ItemCarrito> listaItemCarrito = itemCarritoServiceObj.findAllByDate(fechaInicio, fechaFin);
        System.out.println("lista itemcarrito: " + listaItemCarrito.size());


        double utilidadTotal = 0;

        //Itero la lista para obtener la suma de utilidades
        for (ItemCarrito item : listaItemCarrito){
            utilidadTotal += item.getProducto().getUtilidad() * item.getCantidad();
        }



        model.addAttribute("fechaInicio", fechaInit);
        model.addAttribute("fechaFin", fechaF);
        model.addAttribute("totalVendido", totalVendido);
        model.addAttribute("cantidadVentas", resultados.get(0).getMaximo());
        model.addAttribute("nombreProducto", producto.getNombre());
        model.addAttribute("utilidadTotal", utilidadTotal);


        System.out.println(fechaInicio + ":" + fechaFin);
        return "panel-admin/informes-detalle-admin";
    }


}
