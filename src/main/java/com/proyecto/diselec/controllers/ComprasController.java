package com.proyecto.diselec.controllers;

import com.proyecto.diselec.models.*;
import com.proyecto.diselec.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
@PreAuthorize("hasRole('ROLE_USER')")
public class ComprasController {


    @Autowired
    ProductoService productoServiceObj;

    @Autowired
    CarritoComprasService carritoComprasServiceObj;

    @Autowired
    UsuarioService usuarioServiceObj;

    @Autowired
    ItemCarritoService itemCarritoServiceObj;

    @Autowired
    MetodoPagoService metodoPagoServiceObj;

    @Autowired
    DireccionUsuarioService direccionUsuarioServiceObj;

    @Autowired
    OrdenService ordenServiceObj;


    //Agregar Producto al carrito
    @GetMapping("/add-producto-carrito")
    public String addProductoAlCarrito(HttpServletRequest request, @ModelAttribute("id") Long id,
                                       @ModelAttribute("cantidad") int cantidadSolicitada,
                                       RedirectAttributes redirectAttributes,
                                       Principal principal) {

//        System.out.println("la cantidad solicitada es: " + cantidadSolicitada);
//        System.out.println("el id del producto es: " + id);

        //Se busca el producto
        Producto producto = productoServiceObj.findOne(id);

        //Se localiza el usuario que hace la peticion del carrito
        Usuario usuario = usuarioServiceObj.findByUsername(principal.getName());
        System.out.println("usuario: " + usuario.getUsername());

        //Muestra la cantidad de ese producto que tiene en el carrito de acuerdo al usuario logueado
        int cantidadProductoItemCarritoPorUsuario = itemCarritoServiceObj.listaCarrito(producto, usuario);
        System.out.println("cantidadproductoItemPorUsuario: " + cantidadProductoItemCarritoPorUsuario);

        double subtotal = cantidadSolicitada * producto.getPrecioVenta();

        //Se revisan la cantidad disponible
        if (producto.getCantidad() >= cantidadSolicitada && producto.getCantidad() > 0 && producto.getCantidad() > cantidadProductoItemCarritoPorUsuario
                && producto.getCantidad() >= (cantidadProductoItemCarritoPorUsuario + cantidadSolicitada)) {


            //se revisa si el usuario ya tiene un carrito asignado
            CarritoCompras carritoCompras = carritoComprasServiceObj.findByUsuario(usuario);

            //Si el carrito no existe, se instancia uno
            if (carritoCompras == null) {
                System.out.println("El carrito esta vacio");
                carritoCompras = new CarritoCompras();
                carritoCompras.setUsuario(usuario);
                carritoCompras.setSubTotal(subtotal);
            }
            //Si el carrito existe, se sobreescribe
            else {
                System.out.println("El carrito existe");
                subtotal += carritoCompras.getSubTotal();
                carritoCompras.setSubTotal(subtotal);
                System.out.println(subtotal);
            }

            //Se guarda carrito de compras en BD
            carritoComprasServiceObj.save(carritoCompras);


            double subtotalItemCarrito = cantidadSolicitada * producto.getPrecioVenta();

            //Se revisa en la BD si existe un itemcarrito del mismo productoId y el mismo carritoCompras
            ItemCarrito itemCarritoBD = itemCarritoServiceObj.findByProductoAndCarritoCompras(producto, carritoCompras);
            if (itemCarritoBD != null) {
                itemCarritoBD.setCantidad(itemCarritoBD.getCantidad() + cantidadSolicitada);
                itemCarritoBD.setSubtotal(itemCarritoBD.getSubtotal() + subtotalItemCarrito);
            }
            //Sino aparece se crea uno nuevo
            else {
                //Se crea un itemCarrito
                itemCarritoBD = new ItemCarrito();
                itemCarritoBD.setCantidad(cantidadSolicitada);
                itemCarritoBD.setSubtotal(subtotalItemCarrito);
                itemCarritoBD.setCarritoCompras(carritoCompras);
                itemCarritoBD.setProducto(producto);
            }

            //Se guarda el itemcarrito en BD
            itemCarritoServiceObj.save(itemCarritoBD);


            redirectAttributes.addFlashAttribute("success", "El producto: " + producto.getNombre()
                    + " ha sido añadido correctamente al carrito");
        } else {
            redirectAttributes.addFlashAttribute("warning", "Revisa que la cantidad sea correcta");
        }

        //Obtiene la pagina anterior
        String referer = request.getHeader("Referer");
        return "redirect:" + referer;
    }


    //Modificar Producto del carrito
    @GetMapping("/modificar-producto-carrito")
    public String modificarProductoAlCarrito(HttpServletRequest request, @RequestParam("id") Long id,
                                             @RequestParam("cantidad") int cantidadSolicitada,
                                             RedirectAttributes redirectAttributes,
                                             Principal principal) {

//        System.out.println("la cantidad ajustada es: " + cantidadSolicitada);
//        System.out.println("el id del producto es: " + id);

        //Se busca el producto
        Producto producto = productoServiceObj.findOne(id);

        //Se localiza el usuario que hace la peticion del carrito
        Usuario usuario = usuarioServiceObj.findByUsername(principal.getName());

        //Muestra la cantidad de ese producto que tiene en el carrito de acuerdo al usuario logueado
        int cantidadProductoItemCarritoPorUsuario = itemCarritoServiceObj.listaCarrito(producto, usuario);
        System.out.println("cantidadproductoItemPorUsuario: " + cantidadProductoItemCarritoPorUsuario);

        double subtotal = cantidadSolicitada * producto.getPrecioVenta();

        //Se revisan la cantidad disponible
        if (producto.getCantidad() >= cantidadSolicitada && producto.getCantidad() > 0) {

            //se busca el carrito asignado
            CarritoCompras carritoCompras = carritoComprasServiceObj.findByUsuario(usuario);


            //Se calcula el total
            double subtotalItemCarrito = cantidadSolicitada * producto.getPrecioVenta();

            //Se revisa en la BD itemcarrito del mismo productoId y el mismo carritoCompras
            ItemCarrito itemCarritoBD = itemCarritoServiceObj.findByProductoAndCarritoCompras(producto, carritoCompras);
            itemCarritoBD.setCantidad(cantidadSolicitada);
            itemCarritoBD.setCarritoCompras(carritoCompras);
            itemCarritoBD.setProducto(producto);

            //Se resta el subtotal anterior al carrito de compras
            double nuevoSubtotal = (carritoCompras.getSubTotal() - itemCarritoBD.getSubtotal()) + subtotalItemCarrito;
            carritoCompras.setSubTotal(nuevoSubtotal);

            itemCarritoBD.setSubtotal(subtotalItemCarrito);

            //Se guarda el itemcarrito en BD
            itemCarritoServiceObj.save(itemCarritoBD);


            redirectAttributes.addFlashAttribute("success", "El producto: " + producto.getNombre()
                    + " ha sido modificado correctamente");
        } else {
            redirectAttributes.addFlashAttribute("warning", "Revisa que la cantidad sea correcta");
        }

        //Obtiene la pagina anterior
        String referer = request.getHeader("Referer");
        return "redirect:" + referer;
    }


    // Pagina para ver el carrito de compras actual
    @GetMapping("/carrito")
    public String carritoCompras(Principal principal, Model model) {

        //Se localiza el usuario que esta conectado
        Usuario usuario = usuarioServiceObj.findByUsername(principal.getName());

        //Se localiza el carrito de compras de acuerdo al usuario que esta conectado
        CarritoCompras carritoCompras;

        //Se trae la lista de ItemsCarrito
        List<ItemCarrito> listaItemCarrito = new ArrayList<>();
        try {
            carritoCompras = usuario.getCarritoCompras();
            listaItemCarrito = usuario.getCarritoCompras().getListaItemsCarrito();
        } catch (Exception e) {
            carritoCompras = new CarritoCompras();
            System.out.println("lista null");
        }


        if (listaItemCarrito.size() > 0) {
            model.addAttribute("listaItemCarrito", listaItemCarrito);
        }
        if (carritoCompras != null) {
            model.addAttribute("carritoCompras", carritoCompras);
        }


        //Obtiene los datos para Ultimos productos
        //Obtener los ultimos productos agregados
        List<Producto> listaUltimosProductos = productoServiceObj.findAllByFechaIsBefore(new Date());
        if (listaUltimosProductos.size() > 5){
            listaUltimosProductos = listaUltimosProductos.subList(0,4);
        }


        System.out.println(listaUltimosProductos.size());

        for (Producto obj : listaUltimosProductos){
            System.out.println(obj.getFecha());
        }

        model.addAttribute("listaProductos", listaUltimosProductos);

        return "carrito";
    }


    //Eliminar el producto del carrito
    @GetMapping("/eliminar-producto-carrito/{idProducto}/{idItemCarrito}")
    public String eliminarProductoCarrito(@PathVariable("idProducto") Long idProducto, @PathVariable("idItemCarrito") Long idItemCarrito,
                                          RedirectAttributes redirectAttributes) {
        System.out.println("El id del producto a eliminar es: " + idProducto + " y el del carrito: " + idItemCarrito);

        //Se localiza el itemCarrito que se desea borrar
        ItemCarrito itemCarrito = itemCarritoServiceObj.findOne(idItemCarrito);

        //Se modifican los valores del carrito de compras
        CarritoCompras carritoCompras = itemCarrito.getCarritoCompras();
        double nuevoSubTotal = carritoCompras.getSubTotal() - itemCarrito.getSubtotal();


        //Se borra el id del carrito
        itemCarritoServiceObj.deleteById(idItemCarrito);

        //Se revisa si el carrito de compras esta en ceros el subtotal para eliminarlo completamente o guardar el carrito de compras
        if (nuevoSubTotal == 0) {
            carritoComprasServiceObj.delete(carritoCompras);
        } else {
            carritoCompras.setSubTotal(nuevoSubTotal);
            carritoComprasServiceObj.save(carritoCompras);
        }

        redirectAttributes.addFlashAttribute("success", "Item eliminado satisfactoriamente");
        return "redirect:/carrito";
    }







    // Pagina para proceder a realizar el checkout
    @GetMapping("/checkout")
    public String checkout(Principal principal, Model model,
                           @RequestParam(value = "idMetodoPago", required = false) Long idMetodoPago) {

        int checkbox = 0;
        int envioGratis = 0;
        int envioEstandar = 15000;


        //Se localiza el usuario
        Usuario usuario = usuarioServiceObj.findByUsername(principal.getName());

        //Se localiza el carrito de compras
        CarritoCompras carritoCompras = usuario.getCarritoCompras();

        //Se busca la lista de items
        List<ItemCarrito> listaItemCarrito = usuario.getCarritoCompras().getListaItemsCarrito();

        //Se busca la lista de los metodos de pago
        List<MetodoPago> listaMetodosPago = metodoPagoServiceObj.findAllByUsuario(usuario);
        model.addAttribute("listaMetodosPago", listaMetodosPago);



        //Se busca la direccion de usuario
        DireccionUsuario direccionUsuario = direccionUsuarioServiceObj.findDireccionUsuario(usuario);
        if (direccionUsuario == null) {
            direccionUsuario = new DireccionUsuario();
        }

        model.addAttribute("direccionUsuario", direccionUsuario);



        //Busca por defecto el primer metodo de pago inscrito
        MetodoPago metodoPago;

        //Si viene con un request de seleccion de metodo de pago se usa
        if (idMetodoPago != null){
            metodoPago = metodoPagoServiceObj.findById(idMetodoPago);
        }
        else{
            metodoPago = metodoPagoServiceObj.findById(1L);
        }


        if (metodoPago == null){
            metodoPago = new MetodoPago();
        }

        model.addAttribute("metodoPago", metodoPago);
        model.addAttribute("listaItemCarrito", listaItemCarrito);
        model.addAttribute("carritoCompras", carritoCompras);
        model.addAttribute("checkbox", checkbox);
        model.addAttribute("envioGratis",envioGratis);
        model.addAttribute("envioEstandar", envioEstandar);

        return "checkout";
    }



    @PostMapping("/enviar-orden")
    public String enviarOrden(@ModelAttribute("envio") double valorEnvio,
                              Principal principal,
                              @ModelAttribute("metodoPago") MetodoPago metodoPago,
                              @ModelAttribute("direccionUsuario") DireccionUsuario direccionUsuario,
                              Model model){

        //Se localiza al usuario
        Usuario usuario = usuarioServiceObj.findByUsername(principal.getName());

        //Localizar al carrito
        CarritoCompras carritoCompras = usuario.getCarritoCompras();

        //localizar la lista de item carrito
        List<ItemCarrito> listaItemCarrito = usuario.getCarritoCompras().getListaItemsCarrito();

        //Se modifica la direccion de usuario de acuerdo a lo que se ingrese
        DireccionUsuario direccionUsuarioBD =  direccionUsuarioServiceObj.findDireccionUsuario(usuario);
        if (direccionUsuarioBD == null){
            direccionUsuarioBD = new DireccionUsuario();
        }
        direccionUsuarioBD.setNombre(direccionUsuario.getNombre());
        direccionUsuarioBD.setCiudad(direccionUsuario.getCiudad());
        direccionUsuarioBD.setCodigoPostal(direccionUsuario.getCodigoPostal());
        direccionUsuarioBD.setDepartamento(direccionUsuario.getDepartamento());
        direccionUsuarioBD.setDireccion(direccionUsuario.getDireccion());
        direccionUsuarioBD.setTelefono(direccionUsuario.getTelefono());
        direccionUsuarioBD.setUsuario(usuario);

        //Se guarda en la BD
        direccionUsuarioServiceObj.saveDireccionUsuario(direccionUsuarioBD);


        //Guarda el metodo de pago, compara los numeros de la tarjeta para saber si hay variacion
        MetodoPago metodoPagoBD = metodoPagoServiceObj.findByNumeroTarjeta(metodoPago.getNumeroTarjeta());

        //Si no existe un metodo de pago ya inscrito
        if (metodoPagoBD == null){
            metodoPagoBD = new MetodoPago();
            metodoPagoBD.setTipo(metodoPago.getTipo());
            metodoPagoBD.setNombreTitular(metodoPago.getNombreTitular());
            metodoPagoBD.setNumeroTarjeta(metodoPago.getNumeroTarjeta());
            metodoPagoBD.setCvc(metodoPago.getCvc());
            metodoPagoBD.setMesExpiracion(metodoPago.getMesExpiracion());
            metodoPagoBD.setYearExpiracion(metodoPago.getYearExpiracion());
            metodoPagoBD.setUsuario(usuario);
        }

        //Se guarda en la BD
        metodoPagoServiceObj.save(metodoPagoBD);



        //Se calcula el total con el envio
        double grandTotal = valorEnvio + carritoCompras.getSubTotal();

        //Calcula la fecha estimada de entrega
        LocalDate tiempoEstimadoEntrega;
        if (valorEnvio > 0.0){
            tiempoEstimadoEntrega = LocalDate.now().plusDays(3);
        }
        else{
            tiempoEstimadoEntrega = LocalDate.now();
        }

        Date fechaEntrega = Date.from(tiempoEstimadoEntrega.atStartOfDay(ZoneId.systemDefault()).toInstant());

        Orden orden = new Orden();

        orden.setUsuario(usuario);
        orden.setGrandTotal(grandTotal);
        orden.setFechaEntregaProducto(fechaEntrega);
        orden.setFechaRecepcionOrden(new Date());
        orden.setMetodoEnvio((valorEnvio > 0.0)? "Estandar" : "Recoge en local");
        orden.setMetodoPago(metodoPagoBD);
        orden.setDireccionUsuario(direccionUsuarioBD);

        ordenServiceObj.save(orden);

        for (ItemCarrito obj : listaItemCarrito ){
            obj.setOrden(orden);
            obj.getProducto().setCantidad(obj.getCantidad() - obj.getProducto().getCantidad());
            itemCarritoServiceObj.save(obj);
        }

        model.addAttribute("envioFinal", valorEnvio > 0.0);
        model.addAttribute("fechaEntregaFinal", fechaEntrega);
        model.addAttribute("idOrden",orden.getId());

        //Limpiar el carrito
        carritoComprasServiceObj.limpiarCarrito(carritoCompras);

        //Descontar de productos


        return "orden_exitosa";
    }






}
