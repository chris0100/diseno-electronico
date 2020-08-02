package com.proyecto.diselec.repositories;

import com.proyecto.diselec.models.PasswordResetToken;
import com.proyecto.diselec.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.stream.Stream;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    PasswordResetToken findByTokenAndAsuntoToken(String token,String asuntoToken);

    PasswordResetToken findByUsuario(Usuario usuario);

    Stream<PasswordResetToken> findByFechaExpiracionLessThan(Date now);

    void deleteAllByUsuario(Usuario usuario);

    @Modifying
    @Query("delete from PasswordResetToken t where t.fechaExpiracion <= ?1")
    void deleteAllExpiradosDesde(Date now);
}
