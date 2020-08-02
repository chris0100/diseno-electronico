package com.proyecto.diselec.controllers;


import com.proyecto.diselec.models.PasswordResetToken;
import com.proyecto.diselec.models.Role;
import com.proyecto.diselec.models.Usuario;
import com.proyecto.diselec.services.UsuarioService;
import com.proyecto.diselec.util.MailConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.*;

@Controller
public class LoginController {

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private UsuarioService usuarioServiceObj;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private MailConstructor mailConstructor;

    @Autowired
    private JavaMailSender mailSender;



    // Pagina de inicio de sesion
    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
                        @RequestParam(value = "logout", required = false) String  logout, Model model, Principal principal,
                        RedirectAttributes redirectAttributes, Locale locale){

        if (principal != null){
            redirectAttributes.addFlashAttribute("info", messageSource.getMessage("text.login.already",null, locale));
            return "redirect:/";
        }

        if (error != null){
            System.out.println("Error al loguearse");
            model.addAttribute("error", "nombre de usuario o contraseña incorrecta, por favor vuelva a intentarlo!");
        }

        if (logout != null){
            System.out.println("Haz cerrado sesion");
            model.addAttribute("success", messageSource.getMessage("text.login.logout", null, locale));
        }
        return "login";
    }




    // Formulario de Registro
    @GetMapping("/registro")
    public String registro(Model model){
        Usuario usuario = new Usuario();
        model.addAttribute("usuario", usuario);
        return "registro";
    }




    // Envia formulario de registro
    @PostMapping("/registro")
    public String registroPost(@ModelAttribute("usuario") Usuario usuario,
                               @ModelAttribute("repetirPassword") String repetirPassword, Model model,
                               RedirectAttributes redirectAttributes, HttpServletRequest request){

        if (!usuario.getUsername().trim().isEmpty() && !usuario.getPassword().isEmpty()
                && !usuario.getNombre().trim().isEmpty() && !repetirPassword.trim().isEmpty()){

            // Verifica que coincidan las contraseñas
            if (!usuario.getPassword().equals(repetirPassword)){
                model.addAttribute("error", "Las contraseñas no coinciden");
                return "registro";
            }

            // Si las contraseñas coinciden, se revisa el usario en la BD
            if (usuarioServiceObj.findByUsername(usuario.getUsername()) != null){
                model.addAttribute("error", "El usuario ya se encuentra registrado");
                return "registro";
            }

            // Se realiza la creacion de los roles y demas datos para guardar en la BD
            usuario.setEnabled(false);

            List<Role> listaRole = new ArrayList<>();
            Role objRole = new Role();
            objRole.setAuthority("ROLE_USER");
            listaRole.add(objRole);

            usuario.setListaRoles(listaRole);

            //Se encripta la contraseña
            String passwordEncriptado = passwordEncoder.encode(usuario.getPassword());
            usuario.setPassword(passwordEncriptado);
            usuario.setFechaInscripcion(new Date());

            //Se guarda el usuario en la BD
            usuarioServiceObj.saveUsuario(usuario);


            // Se crea token para habilitar usuario
            String token = UUID.randomUUID().toString();

            // Crea el token en la base de datos con la fecha de expiracion y demas.
            usuarioServiceObj.crearPasswordResetTokenForUser(usuario, token, "nuevo-usuario");

            String appUrl = "http://"+ request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
            String mensaje = "Para confirmar la cuenta da clic en el enlace:";
            String tipo = "nuevo-usuario";
            String asunto = "Confirmacion Nuevo Usuario";

            //Se envia el token por correo electronico
            SimpleMailMessage email = mailConstructor.construirEmailResetToken(appUrl, token, usuario, mensaje, tipo, asunto);
            mailSender.send(email);

        }
        else{
            model.addAttribute("error", "Todos los campos deben estar diligenciados");
            return "registro";
        }

        redirectAttributes.addFlashAttribute("success", "Usuario registrado satisfactoriamente, verifica tu correo para habilitar tu cuenta");
        return "redirect:/login";
    }





    //Se usa cuando se da clic desde el correo al link enviado para confirmar cuenta
    @GetMapping("/nuevo-usuario")
    public String nuevoUsuario(@RequestParam("token") String token, RedirectAttributes redirectAttributes, Model model){

        //Se revisa si el token es valido
        PasswordResetToken elToken = usuarioServiceObj.getPasswordResetToken(token,"nuevo-usuario");
        if (elToken != null){

            //Revisa si el token ha expiro
            if (elToken.getFechaExpiracion().after(new Date())){

                //Se obtiene el usuario a partir del token
                Usuario usuario = elToken.getUsuario();

                //Se habilita el usuario
                usuario.setEnabled(true);
                usuarioServiceObj.saveUsuario(usuario);

                //Destruye el token ya verificado
                usuarioServiceObj.deleteToken(elToken);

                //Redirecciona con mensaje satisfactorio
                redirectAttributes.addFlashAttribute("success", "Cuenta verificada satisfactoriamente, ya puedes loguearte");
                return "redirect:/login";
            }
            else{
                redirectAttributes.addFlashAttribute("error", "El token ya expiro, registrate nuevamente " +
                        "para generar uno nuevo");
                return "redirect:/registro";
            }
        }
        else{
            redirectAttributes.addFlashAttribute("error", "El token no es valido");
            return "redirect:/registro";
        }
    }





    // Muestra el formulario para recuperar contraseña
    @GetMapping("/form-recuperar-password")
    public String formRecuperarPasswword(){
        return "form-recuperar-password";
    }





    //Se ingresa el correo para generar token
    @PostMapping("/generar-token-passsword")
    public String generarTokenPassword(@RequestParam("username") String username, RedirectAttributes redirectAttributes,
                                      HttpServletRequest request, Model model){

        //Se verifica que el username exista en la BD
        Usuario usuario = usuarioServiceObj.findByUsername(username);

        // Si el usuario no esta registrado, envia mensaje de error
        if (usuario == null){
            redirectAttributes.addFlashAttribute("warning", "El usuario no existe en el sistema");
            return "redirect:/";
        }


        //Se crea token
        String token = UUID.randomUUID().toString();

        //Se crea objeto token con fecha de expiracion
        usuarioServiceObj.crearPasswordResetTokenForUser(usuario, token, "olvide-password");

        String appUrl = "http://"+ request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
        String mensaje = "Para reestablecer la contraseña da clic en el enlace:";
        String tipo = "validar-token";
        String asunto = "Reestablecer Password";

        //Se envia el token por correo electronico
        SimpleMailMessage email = mailConstructor.construirEmailResetToken(appUrl, token, usuario, mensaje, tipo, asunto);
        mailSender.send(email);

        model.addAttribute("info", "Revisa tu email para reestablecer el password");
        return "index";
    }





    // Verifica la autenticidad y caducidad del token
    @GetMapping("/validar-token")
    public String ingresarNuevoPassword(@RequestParam("token") String token, RedirectAttributes redirectAttributes,
                                        Model model){
        //Revisa que el token sea valido
        PasswordResetToken elToken = usuarioServiceObj.getPasswordResetToken(token,"olvide-password");


        if (elToken != null){

            System.out.println(elToken.getFechaExpiracion());

            //Revisa si el token ha expiro
            if (elToken.getFechaExpiracion().after(new Date())){
                model.addAttribute("success", "Ya puedes cambiar tu password");
                Long id = elToken.getUsuario().getId();
                Usuario usuario = elToken.getUsuario();


                model.addAttribute("idUsuario", id);
                model.addAttribute("token", elToken.getToken());
                return "nuevo-password";
            }
            else{
                redirectAttributes.addFlashAttribute("error", "El token ya expiro, intenta nuevamente " +
                        "para generar uno nuevo");
                return "redirect:/registro";
            }
        }
        else{
            redirectAttributes.addFlashAttribute("error", "El token no es valido");
            return "redirect:/registro";
        }
    }



    // Se envia el nuevo password para asignar al usuario
    @PostMapping("/reestablecer-password/{idUsuario}/{token}")
    public String nuevoPasswordPost(@ModelAttribute("password") String password,
                                    @ModelAttribute("repetirPassword") String repetirPassword,
                                    @PathVariable("idUsuario") Long id, @PathVariable("token") String token,
                                    HttpServletRequest request, RedirectAttributes redirectAttributes,
                                    Model model) {

        //Obtiene la pagina anterior
        //String referer = request.getHeader("Referer");
        System.out.println("id: " + id);

        //Localiza al usuario
        Usuario usuario = usuarioServiceObj.findById(id);

        if (usuario == null){
            redirectAttributes.addFlashAttribute("error", "El usuario no existe en el sistema");
            return "redirect:/";
        }


        if (!password.trim().isEmpty() && !repetirPassword.trim().isEmpty()) {

            // Verifica que coincidan las contraseñas
            if (!password.equals(repetirPassword)) {
                model.addAttribute("error", "Los password no coinciden");
                return "nuevo-password";
            }

            // Se encripta el password
            String nuevaPassword = passwordEncoder.encode(password);
            usuario.setPassword(nuevaPassword);

            //Se guarda el usuario modificado en la BD
            usuarioServiceObj.saveUsuario(usuario);

            //Se eliminan los token asociados al usuario que son del tipo olvide-password, solo despues de ser correcto
            //el cambio
            usuarioServiceObj.deleteOtrosToken(usuario);

            //Redirecciona para loguearse
            redirectAttributes.addFlashAttribute("success", "Password modificado correctamente, ahora puede loguearse");
            return "redirect:/login";
        }

        model.addAttribute("error", "Los campos deben estar diligenciados");
        return "nuevo-password";
    }


















}
