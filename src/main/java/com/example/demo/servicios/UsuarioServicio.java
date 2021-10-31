package com.example.demo.servicios;

import com.example.demo.entidades.Usuario;
import com.example.demo.excepciones.ErrorServicio;
import com.example.demo.repositorios.UsuarioRepositorio;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsuarioServicio {

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;
  
/////METODO PARA BUSCAR USUARIO POR ID    
    public Usuario buscarUsuarioPorId(String id) throws ErrorServicio {
     
        
        
        
        
        
        
        Optional<Usuario> respuesta = usuarioRepositorio.findById(id);

        if (respuesta != null) {

            Usuario usuario = respuesta.get();
            return usuario;
        } else {
           throw new ErrorServicio("Error al buscar al usuario en cuestión(No encontrado)."); 
        }
      
    
    }
    
}
