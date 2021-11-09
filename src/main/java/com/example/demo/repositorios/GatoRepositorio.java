
package com.example.demo.repositorios;

import com.example.demo.entidades.Gato;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface GatoRepositorio extends JpaRepository<Gato, String> {
    
/////Query para traer a todos los gatos activos de la página.  
    @Query("SELECT c FROM Gato c WHERE c.baja is null")
    public List<Gato> listarGatosActivos();
    
    
/////Query para traer a todos los gatos del usuario.    
    @Query("SELECT c FROM Gato c WHERE c.usuario.id = :id")
    public List<Gato> listarGatosPorUsuario(@Param("id")String idUsuario);
    
/////Query para traer a todos los gatos de una localidad determinada.    
    @Query("SELECT c FROM Gato c WHERE c.usuario.localidad.id = :id")
    public List<Gato> listarGatosPorLocalidad(@Param("id")String idLocalidad);
}
