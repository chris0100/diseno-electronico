package com.proyecto.diselec.repositories;

import com.proyecto.diselec.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {



    Usuario findByUsername(String username);


    //-- Selecciona los usuarios que son tipo role_user
    //select u.id, nombre, username from users u
    //inner join authorities a
    //on u.id = a.user_id where a.authority = 'ROLE_USER';
    //@Modifying
    //@Query("select u from Usuario u fetch all join properties ")
    //List<Usuario> findAllUsuarios();


            //    @Modifying
    //    @Query("delete from PasswordResetToken t where t.fechaExpiracion <= ?1")
    //    void deleteAllExpiradosDesde(Date now);

}
