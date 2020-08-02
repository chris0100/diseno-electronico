package com.proyecto.diselec.util;

import com.proyecto.diselec.models.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;

@Component
public class MailConstructor {

    @Autowired
    private Environment env;

    public SimpleMailMessage construirEmailResetToken(String contextPath, String token, Usuario usuario,
                                                      String mensaje, String tipo, String asunto){
        String url = contextPath + "/"+ tipo + "?token=" + token;
        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(usuario.getUsername());
        email.setSubject(asunto);
        email.setText(mensaje + "\n" + url);
        email.setFrom(env.getProperty("support.email"));
        return email;
    }


}
