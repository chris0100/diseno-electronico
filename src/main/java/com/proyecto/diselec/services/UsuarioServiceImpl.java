package com.proyecto.diselec.services;

import com.proyecto.diselec.models.PasswordResetToken;
import com.proyecto.diselec.models.Usuario;
import com.proyecto.diselec.repositories.PasswordResetTokenRepository;
import com.proyecto.diselec.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UsuarioServiceImpl implements UsuarioService {


    @Autowired
    private UsuarioRepository usuarioRepositoryObj;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepositoryObj;



    @Override
    @Transactional
    public void saveUsuario(Usuario usuario) {
        usuarioRepositoryObj.save(usuario);
    }


    @Override
    @Transactional(readOnly = true)
    public Usuario findByUsername(String username) {
        return usuarioRepositoryObj.findByUsername(username);
    }


    @Override
    public void crearPasswordResetTokenForUser(Usuario username, String token, String asuntoToken) {
        final PasswordResetToken miToken = new PasswordResetToken(token,username, asuntoToken);
        passwordResetTokenRepositoryObj.save(miToken);
    }


    @Override
    public PasswordResetToken getPasswordResetToken(String token, String asuntoToken) {
        return passwordResetTokenRepositoryObj.findByTokenAndAsuntoToken(token,asuntoToken);
    }


    @Override
    public Usuario findById(Long id) {
        return usuarioRepositoryObj.findById(id).get();
    }


    @Override
    public void deleteToken(PasswordResetToken token) {
        passwordResetTokenRepositoryObj.delete(token);
    }

    @Transactional
    @Override
    public void deleteOtrosToken(Usuario usuario) {
        passwordResetTokenRepositoryObj.deleteAllByUsuario(usuario);
    }

    @Override
    public List<Usuario> findAllUsers() {
        return usuarioRepositoryObj.findAll();
    }
}
