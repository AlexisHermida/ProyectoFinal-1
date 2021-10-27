package com.example.demo.servicios;

import com.example.demo.entidades.Foto;
import com.example.demo.entidades.Usuario;
import com.example.demo.enums.Raza;
import com.example.demo.enums.SexoAnimal;
import com.example.demo.excepciones.WebException;
import com.example.demo.repositorios.GatoRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GatoServicio {

    @Autowired
    private GatoRepositorio gatoRepositorio;

    @Autowired
    private UsuarioServicio usuarioServicio;

    
/////METODO PARA CREAR A UN GATO    
    public void crearMascota(String idUsuario, String nombre, Raza raza, SexoAnimal sexo, Integer anios, Integer meses, Foto fotoPrincipal, String descripcion) {

        Usuario usuario = usuarioServicio.buscarUsuarioPorId(idUsuario);
        
        
        
        if (usuario != null) {
            
            
            
            
            
            
            
        }
        
        
        
        
    }

    
    
    
/////METODO PARA VALIDAR ATRIBUTOS DEL GATO    
    public void validar(String nombre, Raza raza, Integer edad, SexoAnimal sexo, Integer anios, Integer meses) throws WebException {

        if (nombre == null || nombre.trim().isEmpty()) {
            throw new WebException("No ha ingresado un nombre.");
        }

        if (raza == null) {
            throw new WebException("No ha ingresado una raza valida.");
        }

        if (sexo == null) {
            throw new WebException("No ha ingresado un sexo valido.");
        }

        if (meses != null) {

            if (meses > 11) {
                throw new WebException("Debe ingresar de 1 a 11 meses.");
            }
        }

        if (anios != null) {
            if (anios > 16) {
                throw new WebException("Debe ingresar de 1 a 16 años.");
            }
        }

        if (anios == null && meses == null) {
            throw new WebException("Debe ingresar al menos una edad para su gato.");
        }

    }

}
