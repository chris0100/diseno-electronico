package com.proyecto.diselec.services;

import com.proyecto.diselec.models.PasswordResetToken;
import com.proyecto.diselec.models.Usuario;

import java.util.List;

public interface UsuarioService {

    void saveUsuario(Usuario usuario);

    Usuario findByUsername(String username);

    Usuario findById(Long id);

    List<Usuario> findAllUsers();

    void crearPasswordResetTokenForUser(final Usuario username, final String token, final String asuntoToken);

    PasswordResetToken getPasswordResetToken(final String token, final String asuntoToken);

    void deleteToken(PasswordResetToken token);

    void deleteOtrosToken(Usuario usuario);


}
