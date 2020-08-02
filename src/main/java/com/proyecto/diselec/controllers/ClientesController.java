package com.proyecto.diselec.controllers;

import com.proyecto.diselec.models.*;
import com.proyecto.diselec.services.*;
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
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/panel-cliente")
@PreAuthorize("hasRole('ROLE_USER')")
@SessionAttributes("orden")
public class ClientesController {

    @Autowired
    private UsuarioService usuarioServiceObj;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private UploadFileService uploadFileServiceObj;

    @Autowired
    private DireccionUsuarioService direccionUsuarioServiceObj;

    @Autowired
    private MetodoPagoService metodoPagoServiceObj;

    @Autowired
    private OrdenService ordenServiceObj;

    @Autowired
    private ItemCarritoService itemCarritoServiceObj;


    // Pagina para ver el panel principal del cliente
    @GetMapping("")
    public String panelCliente(Principal principal, Model model) {
        Usuario usuario = usuarioServiceObj.findByUsername(principal.getName());
        model.addAttribute("usuario", usuario);

        return "panel-clientes/panel-cliente";
    }




    // Pagina para editar datos del cliente
    @GetMapping("/editar-perfil-cliente")
    public String editarPerfil(Principal principal, Model model) {
        Usuario usuario = usuarioServiceObj.findByUsername(principal.getName());
        model.addAttribute("usuario", usuario);

        return "panel-clientes/editar-perfil";
    }




    //Se envian los datos del formulario
    @PostMapping("/editar-perfil-cliente")
    public String editarPerfilClientePost(@ModelAttribute("usuario") Usuario usuario, RedirectAttributes redirectAttributes,
                                          Model model) {
        System.out.println(usuario.toString());

        if (!usuario.getNombre().trim().isEmpty() && !usuario.getUsername().trim().isEmpty()) {
            Usuario usuarioOriginal = usuarioServiceObj.findById(usuario.getId());

            // Se ajustan las modificaciones del formulario
            usuarioOriginal.setNombre(usuario.getNombre());
            usuarioOriginal.setTelefono(usuario.getTelefono());
            usuarioOriginal.setDescripcion(usuario.getDescripcion());

            //Se guardan los cambios en la BD
            usuarioServiceObj.saveUsuario(usuarioOriginal);

            redirectAttributes.addFlashAttribute("success", "Usuario modificado satisfactoriamente");
            return "redirect:/panel-cliente/";
        }
        model.addAttribute("warning", "Los campos de nombre e email deben estar diligenciados");
        return "panel-clientes/editar-perfil";
    }





    // Pagina para cambiar password
    @GetMapping("/cambiar-password-cliente")
    public String cambiarPassword() {
        return "panel-clientes/cambiar-password-cliente";
    }





    //Envio de passwords para cambio
    @PostMapping("/cambiar-password-cliente")
    public String cambiarPasswordPost(@ModelAttribute("anteriorPassword") String anteriorPassword,
                                      @ModelAttribute("nuevoPassword") String nuevoPassword, Model model,
                                      RedirectAttributes redirectAttributes, Principal principal) {

        //Valida que los campos esten diligenciados
        if (!anteriorPassword.trim().isEmpty() && !nuevoPassword.trim().isEmpty()) {

            //Trae el password encriptado de la BD
            Usuario usuario = usuarioServiceObj.findByUsername(principal.getName());
            String passwordEncriptado = usuario.getPassword();

            //Revisa que el anterior password coincida con el de la BD
            if (passwordEncoder.matches(anteriorPassword, passwordEncriptado)) {
                //Se asigna el password encriptado
                usuario.setPassword(passwordEncoder.encode(nuevoPassword));

                //Se guarda en la BD
                usuarioServiceObj.saveUsuario(usuario);

                //Se redirecciona a la pagina panel clientes
                redirectAttributes.addFlashAttribute("success", "El Password ha sido modificado satisfactoriamente");
                return "redirect:/panel-cliente/";

            } else {
                model.addAttribute("error", "El password anterior no es correcto");
                return "panel-clientes/cambiar-password-cliente";
            }
        } else {
            model.addAttribute("error", "Los campos deben estar diligenciados");
            return "panel-clientes/cambiar-password-cliente";
        }
    }





    // Pagina para cambiar la imagen del cliente
    @GetMapping("/imagen-cliente")
    public String imagenCliente(Model model, Principal principal) {
        Usuario usuario = usuarioServiceObj.findByUsername(principal.getName());
        model.addAttribute("usuario", usuario);

        return "panel-clientes/imagen-cliente";
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





    // Se envia la foto para ser cargada
    @PostMapping("/cargar-imagen-cliente")
    public String cargarImagenAdmin(@RequestParam("file") MultipartFile foto, RedirectAttributes redirectAttributes,
                                    Principal principal, Model model) {
        if (!foto.isEmpty()) {
            Usuario usuario = usuarioServiceObj.findByUsername(principal.getName());

            //Verificar que si sea una imagen
            try {
                InputStream input = foto.getInputStream();

                try {
                    ImageIO.read(input).toString();
                } catch (Exception e) {
                    model.addAttribute("error", "El archivo no es una imagen");
                    model.addAttribute("usuario", usuario);
                    return "panel-clientes/imagen-cliente";
                }
            } catch (Exception e) {
                System.out.println("primero no es un archivo valido");
            }

            //Se borra la anterior foto si existe
            uploadFileServiceObj.delete(usuario.getImagen());

            String uniqueFilename = null;

            try {
                uniqueFilename = uploadFileServiceObj.copy(foto);
            } catch (IOException e) {
                e.printStackTrace();
            }

            //Se guarda la foto en el usuario y en la BD
            usuario.setImagen(uniqueFilename);
            usuarioServiceObj.saveUsuario(usuario);

            //Se redirecciona correctamente
            redirectAttributes.addFlashAttribute("success", "Imagen cargada correctamente");
            return "redirect:/panel-cliente/";
        } else {
            redirectAttributes.addFlashAttribute("info", "No se ha cargado ninguna imagen");
            return "redirect:/panel-cliente/";
        }
    }






    // Pagina para ver las direcciones de envio
    @GetMapping("/direccion-cliente")
    public String direccionCliente(Model model, Principal principal) {
        //Se localiza el usuario
        Usuario usuario = usuarioServiceObj.findByUsername(principal.getName());

        // Se ubica la direccion del usuario
        DireccionUsuario direccionUsuario = direccionUsuarioServiceObj.findDireccionUsuario(usuario);

        if (direccionUsuario == null) {
            direccionUsuario = new DireccionUsuario();
        }

        //Se envian como atributo
        model.addAttribute("direccionUsuario", direccionUsuario);
        return "panel-clientes/direcciones-cliente";
    }





    //Agregar la modificacion de la direccion del cliente
    @PostMapping("/agregar-direccion-cliente")
    public String agregarDireccionCliente(@ModelAttribute("direccionUsuario") DireccionUsuario direccionUsuario,
                                          Principal principal, RedirectAttributes redirectAttributes) {

        // Localizamos el usuario
        Usuario usuario = usuarioServiceObj.findByUsername(principal.getName());

        //Localizamos la direccion original
        DireccionUsuario direccionUsuarioOriginal = direccionUsuarioServiceObj.findDireccionUsuario(usuario);

        //Guardar por primera vez
        if (direccionUsuarioOriginal == null) {
            direccionUsuario.setUsuario(usuario);
            direccionUsuarioServiceObj.saveDireccionUsuario(direccionUsuario);
        }
        //Editar una direccion
        else {
            direccionUsuarioOriginal.setNombre(direccionUsuario.getNombre());
            direccionUsuarioOriginal.setDireccion(direccionUsuario.getDireccion());
            direccionUsuarioOriginal.setTelefono(direccionUsuario.getTelefono());
            direccionUsuarioOriginal.setCodigoPostal(direccionUsuario.getCodigoPostal());
            direccionUsuarioOriginal.setDepartamento(direccionUsuario.getDepartamento());
            direccionUsuarioOriginal.setCiudad(direccionUsuario.getCiudad());
            direccionUsuarioServiceObj.saveDireccionUsuario(direccionUsuarioOriginal);
        }

        redirectAttributes.addFlashAttribute("success", "Cambios realizados correctamente");
        return "redirect:/panel-cliente/";
    }





    // Pagina para ver metodos de pago
    @GetMapping("/metodos-pago-cliente")
    public String metodosPagoCliente(Principal principal, Model model) {
        Usuario usuario = usuarioServiceObj.findByUsername(principal.getName());

        //Localizar los metodos de pago guardados
        List<MetodoPago> listaMetodosPago = metodoPagoServiceObj.findAllByUsuario(usuario);

        System.out.println(usuario.getListaRoles().size());
        System.out.println(usuario.getListaMetodosPago().size());

        model.addAttribute("listaMetodosPago", listaMetodosPago);
        //model.addAttribute("hola", "hola");

        return "panel-clientes/metodos-pago-cliente";
    }





    // Pagina para añadir nuevo metodo de pago
    @GetMapping("/form-metodo-pago")
    public String formMetodoPagoCliente(@RequestParam(value = "id", required = false) Long id, Model model) {

        MetodoPago metodoPago;

        //Busca el id
        if (id != null) {
            metodoPago = metodoPagoServiceObj.findById(id);
        }
        else {
            metodoPago = new MetodoPago();
        }

        model.addAttribute("metodoPago", metodoPago);

        return "panel-clientes/form-metodo-pago-clientes";
    }


    //Añadir metodo de pago
    @PostMapping("/enviar-metodo-pago")
    public String enviarMetodoPago(@ModelAttribute("metodoPago") MetodoPago metodoPago, Principal principal,
                                   RedirectAttributes redirectAttributes, Model model) {
        System.out.println(metodoPago.toString());

        //Revisa que todos los campos esten diligenciados
        if (!metodoPago.getTipo().trim().isEmpty() && !metodoPago.getNombreTitular().trim().isEmpty() &&
                !metodoPago.getNumeroTarjeta().trim().isEmpty() && metodoPago.getNumeroTarjeta().length() == 16 && !metodoPago.getCvc().trim().isEmpty() &&
                !metodoPago.getMesExpiracion().trim().isEmpty() && !metodoPago.getYearExpiracion().trim().isEmpty()) {

            //Localizar usuario
            Usuario usuario = usuarioServiceObj.findByUsername(principal.getName());

            //Si el metodo de pago existe, se localiza su original a traves de su id
            if (metodoPago.getId() != null) {
                MetodoPago metodoPagoOriginal = metodoPagoServiceObj.findById(metodoPago.getId());

                metodoPagoOriginal.setTipo(metodoPago.getTipo());
                metodoPagoOriginal.setNombreTitular(metodoPago.getNombreTitular());
                metodoPagoOriginal.setNumeroTarjeta(metodoPago.getNumeroTarjeta());
                metodoPagoOriginal.setCvc(metodoPago.getCvc());
                metodoPagoOriginal.setMesExpiracion(metodoPago.getMesExpiracion());
                metodoPagoOriginal.setYearExpiracion(metodoPago.getYearExpiracion());

                //Guardar en la BD
                metodoPagoServiceObj.save(metodoPagoOriginal);
                redirectAttributes.addFlashAttribute("success", "Metodo de pago modificado correctamente");
                return "redirect:/panel-cliente/metodos-pago-cliente";
            }
            //Se guarda en la BD
            metodoPago.setUsuario(usuario);
            metodoPagoServiceObj.save(metodoPago);

            //Se redirecciona y se envia mensaje correcto
            redirectAttributes.addFlashAttribute("success", "Metodo de pago creado correctamente");
            return "redirect:/panel-cliente/metodos-pago-cliente";
        } else {
            model.addAttribute("metodoPago", metodoPago);
            model.addAttribute("warning", "Todos los campos deben estar correctamente diligenciados");
            return "panel-clientes/form-metodo-pago-clientes";
        }
    }


    //Eliminar un metodo de pago
    @GetMapping("/metodos-pago-cliente/eliminar-metodo-pago/{id}")
    public String eliminarMetodoPago(@PathVariable("id") Long id, RedirectAttributes redirectAttributes){
        System.out.println("el id a eliminar es: " + id);

        //Localiza el metodo de pago por el id y lo borra
        if(!metodoPagoServiceObj.deleteById(id)){
            //eliminacion no exitosa
            redirectAttributes.addFlashAttribute("error", "No se ha podido eliminar el metodo de pago");
            return "redirect:/panel-cliente/metodos-pago-cliente";
        }

        redirectAttributes.addFlashAttribute("success", "Metodo de pago eliminado correctamente");
        return "redirect:/panel-cliente/metodos-pago-cliente";
    }





    // Pagina para ver las ordenes del cliente
    @GetMapping("/ordenes-cliente")
    public String ordenesCliente(Model model, Principal principal) {

        //Se localiza el usuario
        Usuario usuario = usuarioServiceObj.findByUsername(principal.getName());

        //Se buscan las ordenes del usuario
        List<Orden> listaOrdenes = ordenServiceObj.findAllByUsuario(usuario);

        model.addAttribute("listaOrdenes", listaOrdenes);

        return "panel-clientes/ordenes-cliente";
    }






    // Pagina para ver los detalles de orden del cliente
    @GetMapping("/ordenes-detalle-cliente/{id}")
    public String ordenesDetalleCliente(@PathVariable("id") Long id, Principal principal, Model model) {

        //Obtener la orden a traves del id
        Orden orden = ordenServiceObj.findOne(id);

        //Obtener la lista del carrito a traves del id de la orden
        List<ItemCarrito> listaItemCarrito = itemCarritoServiceObj.findAllByOrden(orden);

        //Obtener el tiempo restante
        int cantidadRestante = orden.getFechaEntregaProducto().compareTo(new Date());
        System.out.println(cantidadRestante + " cantidad restante");

        if (cantidadRestante < 0){
            cantidadRestante = 0;
        }

        //Calculo de valores de envio y valor subtotal
        double valorEnvio = (orden.getMetodoEnvio().equals("Estandar")? 15000:0);
        double subtotal = orden.getGrandTotal() - valorEnvio;

        //Generar fecha actual
        //Date fechaActual =

        //formatear fecha
        SimpleDateFormat dateP = new SimpleDateFormat("dd/MMM/yyyy");
        String formateada = dateP.format(new Date());
        model.addAttribute("fechaFormateada", formateada);


        model.addAttribute("orden", orden);
        model.addAttribute("listaItemCarrito", listaItemCarrito);
        model.addAttribute("cantidadRestante", cantidadRestante);
        model.addAttribute("valorEnvio", valorEnvio);
        model.addAttribute("subtotal", subtotal);

        return "panel-clientes/ordenes-detalle-cliente";
    }


}
