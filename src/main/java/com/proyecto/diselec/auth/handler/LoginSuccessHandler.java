package com.proyecto.diselec.auth.handler;

import com.proyecto.diselec.models.Usuario;
import com.proyecto.diselec.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.support.SessionFlashMapManager;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.Locale;

@Component
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    //Ubicacion de los messages
    @Autowired
    private MessageSource messageSource;

    //Variable del local
    @Autowired
    private LocaleResolver localeResolver;

    @Autowired
    private UsuarioService usuarioServiceObj;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        //Para envio de mensajes flash(a otra pagina)
        SessionFlashMapManager flashMapManager = new SessionFlashMapManager();
        FlashMap flashMap = new FlashMap();


        //Localiza el usuario en la BD para colocarlo en el mensaje exitoso
        Usuario usuario = usuarioServiceObj.findByUsername(authentication.getName());

        //Guarda la ultima conexion del usuario
        usuario.setFechaUltimaConexion(new Date());
        usuarioServiceObj.saveUsuario(usuario);


        //locale ES-EN-DE(para la ubicacion de los messages.properties)
        Locale locale = localeResolver.resolveLocale(request);

        //Mensaje de Login exitoso
        String mensaje = String.format(messageSource.getMessage("text.login.success", null, locale), usuario.getNombre());

        //Envia mensajes a traves del flash
        flashMap.put("success", mensaje);
        flashMapManager.saveOutputFlashMap(flashMap, request, response);

        //Super de la heredacion
        super.onAuthenticationSuccess(request,response,authentication);
    }
}
