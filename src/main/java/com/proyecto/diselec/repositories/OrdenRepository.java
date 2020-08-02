package com.proyecto.diselec.repositories;

import com.proyecto.diselec.models.Orden;
import com.proyecto.diselec.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface OrdenRepository extends JpaRepository<Orden, Long> {

    List<Orden> findByUsuario(Usuario usuario);


    @Query("select sum(f.GrandTotal) from Orden f where f.fechaRecepcionOrden  between ?1 and ?2")
    double findByDates(Date fechaInicio, Date fechaFin);

}
