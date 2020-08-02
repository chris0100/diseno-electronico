package com.proyecto.diselec.services;

import com.proyecto.diselec.models.Role;
import com.proyecto.diselec.models.Usuario;
import com.proyecto.diselec.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service("jpaUserDetailsService")
public class JpaUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepositoryObj;


    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //Busca en la BD el usuario
        Usuario usuario = usuarioRepositoryObj.findByUsername(username);


        //Si el usuario no es encontrado, lanza error de no encontrado
        if (usuario == null){
            System.out.println("El usuario no existe en el sistema");
            throw new UsernameNotFoundException("Usuario: " + username + " no existe en el sistema");
        }

        //Revisa que el usuario este habilitado, despues de verificar el link enviado al email
        if (!usuario.getEnabled()){
            System.out.println("El usuario no esta habilitado, verificar correo");
            throw new UsernameNotFoundException("El usuario no esta habilitado");
        }

        // Crea lista de authorities
        List<GrantedAuthority> authorities = new ArrayList<>();

        // añade el rol a la lista de authorities
        for (Role role : usuario.getListaRoles()){
            authorities.add(new SimpleGrantedAuthority(role.getAuthority()));
        }

        //Si no se añadieron authorities, estara vacio
        if (authorities.isEmpty()){
            System.out.println("El usuario no tiene authorities guardados");
            throw new UsernameNotFoundException("Error en el login: usuario " + username.toUpperCase() +
                    " no tiene roles asignados");
        }

        // Retorna el usuario encontrado correctamente
        return new User(usuario.getUsername(), usuario.getPassword(), usuario.getEnabled(),
                true, true, true, authorities);
    }
}














