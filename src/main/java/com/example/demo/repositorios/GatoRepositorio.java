
package com.example.demo.repositorios;

import com.example.demo.entidades.Gato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface GatoRepositorio extends JpaRepository<Gato, String> {
    
}
