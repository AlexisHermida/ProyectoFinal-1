
package com.example.demo.servicios;

import com.example.demo.Entidades.Foto;
import com.example.demo.Entidades.Localidad;
import com.example.demo.Entidades.Usuario;
import com.example.demo.Errores.ErrorServicio;
import com.example.demo.Repositorio.LocalidadRepositorio;
import com.example.demo.Repositorio.UsuarioRepositorio;
import java.util.Date;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


@Service
public class UsuarioServicio {
    private Usuario usuario;
    @Autowired
    private LocalidadRepositorio localidadRepositorio;
    @Autowired
    private FotoServicio fotoServicio;
    @Autowired
    private UsuarioRepositorio usuarioRepositorio;
    
    
    //REGISTRAR USUARIO
     @Transactional
    public void registrar(MultipartFile archivo,String nombre,int edad,String numero, String apellido, String email, String clave,String clave2,String id) throws ErrorServicio {

        Localidad localidad= localidadRepositorio.getOne(id);
        
        validar(nombre, apellido, email, clave,clave2,localidad);
        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setApellido(apellido);
        usuario.setEdad(edad);
        usuario.setNumero(numero);
        usuario.setEmail(email);
        usuario.setLocalidad(localidad);
        usuario.setActivo(true);
        String encriptida=new BCryptPasswordEncoder().encode(clave);
        usuario.setClave(encriptida);
        
        usuario.setCreado(new Date());
        
        
        
        Foto foto=fotoServicio.guardar(archivo);
        usuario.setFoto(foto);

        
        
        usuarioRepositorio.save(usuario);
        
         
    }
//MODIFICAR USUARIO
     @Transactional
    public void modificar(MultipartFile archivo, String id, String nombre,int edad,String numero, String apellido, String email, String clave,String clave2,String idLocalidad) throws ErrorServicio {

        Localidad localidad= localidadRepositorio.getOne(idLocalidad);
        
        validar(nombre, apellido, email, clave,clave2,localidad);

        Optional<Usuario> respuesta = usuarioRepositorio.findById(id);
        if (respuesta.isPresent()) {

            Usuario usuario = respuesta.get();
            usuario.setNombre(nombre);
            usuario.setEdad(edad);
            usuario.setNumero(numero);
            usuario.setApellido(apellido);
            usuario.setEmail(email);
            usuario.setLocalidad(localidad);
            
            String encriptida=new BCryptPasswordEncoder().encode(clave);
            usuario.setClave(encriptida);
            
            String idFoto=null;
            if (usuario.getFoto() !=null) {
                idFoto=usuario.getFoto().getId();
            }
            
            Foto foto= fotoServicio.actualizar(idFoto, archivo);
            usuario.setFoto(foto);
            
            usuarioRepositorio.save(usuario);
        } else {
            throw new ErrorServicio("No se encontro el usuario");
        }

    }
    
    ///Eliminar USUARIO
        @Transactional
    public void deshabilitar(String id) throws ErrorServicio {

        Optional<Usuario> respuesta = usuarioRepositorio.findById(id);
        if (respuesta.isPresent()) {

            Usuario usuario = respuesta.get();
            usuario.setActivo(false);
            usuarioRepositorio.save(usuario);
        } else {
            throw new ErrorServicio("No se encontro el usuario");
        }

    }
     
    //ACTIVAR USUARIO
    @Transactional
    public void habilitar(String id) throws ErrorServicio {

        Optional<Usuario> respuesta = usuarioRepositorio.findById(id);
        if (respuesta.isPresent()) {

            Usuario usuario = respuesta.get();
            usuario.setActivo(true);
            usuarioRepositorio.save(usuario);
        } else {
            throw new ErrorServicio("No se encontro el usuario");
        }

    }
       
    //BUSCAR USUARIO POR ID
    public Usuario buscarPorId(String id) throws ErrorServicio {
		Optional<Usuario> respuesta = usuarioRepositorio.findById(id);
		if(respuesta.isPresent()) {
			Usuario user = respuesta.get();
			return user;
		} else {
			throw new ErrorServicio("No se encontro al usuario solicitado.");
		}
	}

           //Verificacion si es que los datos del front estan correcto
    private void validar(String nombre, String apellido, String email, String clave,String clave2,Localidad localidad) throws ErrorServicio {
        if (nombre == null || nombre.isEmpty()) {
            throw new ErrorServicio("El nombre del usuario no pude ser nulo");
        }
        if (apellido == null || apellido.isEmpty()) {
            throw new ErrorServicio("El apellido del usuario no pude ser nulo");
        }
        if (email == null || email.isEmpty()) {
            throw new ErrorServicio("El mail del usuario no pude ser nulo");
        }
        if (clave == null || clave.isEmpty() || clave.length() <= 6) {
            throw new ErrorServicio("La clave del usuario no pude ser nulo y tiene que tener mas de 6 digitos");
        }
        if (!clave.equals(clave2)) {
            throw new ErrorServicio("Las claves no coinciden");
        }
        if (localidad==null) {
            throw new ErrorServicio("No se encontro la zona solicitada");
        }
         
    }
       
}
