package com.example.demo.servicios;

import com.example.demo.entidades.Foto;
import com.example.demo.entidades.Localidad;
import com.example.demo.entidades.Usuario;
import com.example.demo.enums.PrivilegiosUsuario;
import com.example.demo.enums.SexoHumano;
import com.example.demo.excepciones.ErrorServicio;
import com.example.demo.repositorios.LocalidadRepositorio;
import com.example.demo.repositorios.UsuarioRepositorio;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UsuarioServicio implements UserDetailsService {

    @Autowired
    private NotificacionServicio notificacionServicio;

    @Autowired
    private LocalidadRepositorio localidadRepositorio;

    @Autowired
    private FotoServicio fotoServicio;

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    //Registrar usuario.
    @Transactional
    public void registrar(MultipartFile archivo, String username, String nombre, Integer numeroSexo, String edad, String numero, String apellido, String email, String clave, String clave2, String idLocalidad) throws ErrorServicio {

        Localidad localidad = localidadRepositorio.getOne(idLocalidad);

        validar(username, nombre, numeroSexo, edad, numero, apellido, email, clave, clave2, localidad);

        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setApellido(apellido);
        usuario.setEdad(edad);
        usuario.setNumero(numero);
        usuario.setEmail(email);
        usuario.setSexo(elegirSexo(numeroSexo));
        usuario.setUsername(username);
        usuario.setPrivilegios(PrivilegiosUsuario.USER);
        usuario.setLocalidad(localidad);
        usuario.setCreado(new Date());

        String encriptida = new BCryptPasswordEncoder().encode(clave);
        usuario.setClave(encriptida);

        usuario.setCreado(new Date());

        Foto foto = fotoServicio.guardar(archivo);
        usuario.setFoto(foto);

        usuarioRepositorio.save(usuario);
        notificacionServicio.notificar(usuario.getId());

    }
    //Modificar usuario.

    @Transactional
    public void modificar(MultipartFile archivo, String username, String idUsuario, String nombre, Integer numeroSexo, String edad, String numero, String apellido, String email, String clave, String clave2, String idLocalidad) throws ErrorServicio {

        Localidad localidad = localidadRepositorio.getOne(idLocalidad);

        validar(username, nombre, numeroSexo, edad, numero, apellido, email, clave, clave2, localidad);

        Optional<Usuario> respuesta = usuarioRepositorio.findById(idUsuario);
        if (respuesta.isPresent()) {

            Usuario usuario = respuesta.get();
            usuario.setNombre(nombre);
            usuario.setEdad(edad);
            usuario.setNumero(numero);
            usuario.setApellido(apellido);
            usuario.setEmail(email);
            usuario.setSexo(elegirSexo(numeroSexo));
            usuario.setPrivilegios(PrivilegiosUsuario.USER);
            usuario.setLocalidad(localidad);

            String encriptida = new BCryptPasswordEncoder().encode(clave);
            usuario.setClave(encriptida);

            String idFoto = null;
            if (usuario.getFoto() != null) {
                idFoto = usuario.getFoto().getId();
            }

            Foto foto = fotoServicio.actualizar(idFoto, archivo);
            usuario.setFoto(foto);

            usuario.setEditado(new Date());
            usuarioRepositorio.save(usuario);
        } else {
            throw new ErrorServicio("No se encontro el usuario");
        }

    }

    ///Deshabilitar usuario.
    @Transactional
    public void deshabilitar(String id) throws ErrorServicio {

        Optional<Usuario> respuesta = usuarioRepositorio.findById(id);
        if (respuesta.isPresent()) {

            Usuario usuario = respuesta.get();
            usuario.setBaja(new Date());
            usuarioRepositorio.save(usuario);
        } else {
            throw new ErrorServicio("No se encontro el usuario a deshabilitar.");
        }

    }

    //Activar usuario.
    @Transactional
    public void habilitar(String id) throws ErrorServicio {

        Optional<Usuario> respuesta = usuarioRepositorio.findById(id);
        if (respuesta.isPresent()) {

            Usuario usuario = respuesta.get();
            usuario.setBaja(null);
            usuarioRepositorio.save(usuario);
        } else {
            throw new ErrorServicio("No se encontro el usuario a activar.");
        }

    }

    //Buscar usuario por email
    @Transactional(readOnly = true)
    public Usuario buscarUsuarioPorEmail(String email) throws ErrorServicio {

        Usuario usuario = usuarioRepositorio.buscarUsuarioPorEmail(email);
        return usuario;

    }

    //Buscar usuario por id
    @Transactional(readOnly = true)
    public Usuario buscarUsuarioPorId(String id) throws ErrorServicio {
        Optional<Usuario> respuesta = usuarioRepositorio.findById(id);
        if (respuesta.isPresent()) {
            Usuario user = respuesta.get();
            return user;
        } else {
            throw new ErrorServicio("No se encontro al usuario en cuestión.");
        }
    }

    //Buscar usuario por id para el email
    @Transactional(readOnly = true)
    public Usuario buscarUsuarioPorIdParaEmail(String id) {
        Optional<Usuario> respuesta = usuarioRepositorio.findById(id);
        if (respuesta.isPresent()) {
            Usuario user = respuesta.get();
            return user;
        } else {
            return null;
        }
    }

    //Guardar usuario en la base de datos
    @Transactional
    public void guardarUsuario(Usuario usuario) {
        usuarioRepositorio.save(usuario);
    }

    //Verificacion para ver si  los datos del front estan en lo correcto.
    private void validar(String username, String nombre, Integer sexo, String edad, String numero, String apellido, String email, String clave, String clave2, Localidad localidad) throws ErrorServicio {

        if (sexo == null) {
            throw new ErrorServicio("No ha ingresado un sexo valido.");
        }

        if (edad == null || edad.trim().isEmpty()) {
            throw new ErrorServicio("La edad no puede ser nula");
        }

        if (Integer.valueOf(edad) < 12) {
            throw new ErrorServicio("La edad no puede ser menor a doce años.");
        }

        if (numero == null || numero.trim().isEmpty()) {
            throw new ErrorServicio("El numero de telefono no puede ser nulo.");
        }

        if (username == null || username.isEmpty()) {
            throw new ErrorServicio("El nombre de usuario no puede ser nulo.");
        }

        if (usuarioRepositorio.buscarUsuarioPorUsername(username) != null) {
            throw new ErrorServicio("El nombre de usuario ingresado ya existe.");
        }

        if (nombre == null || nombre.isEmpty()) {
            throw new ErrorServicio("El nombre del usuario no puede ser nulo.");
        }

        if (apellido == null || apellido.isEmpty()) {
            throw new ErrorServicio("El apellido del usuario no puede ser nulo.");
        }

        if (email == null || email.isEmpty()) {
            throw new ErrorServicio("El email del usuario no puede ser nulo.");
        }

        if (usuarioRepositorio.buscarUsuarioPorEmail(email) != null) {
            throw new ErrorServicio("El email ingresado ya existe.");
        }

        if (clave == null || clave.isEmpty() || clave.length() <= 6) {
            throw new ErrorServicio("La clave no puede ser nula y debe tener más de 6 digitos.");
        }

        if (!clave.equals(clave2)) {
            throw new ErrorServicio("Las claves no coinciden.");
        }

        if (localidad == null) {
            throw new ErrorServicio("No ha ingresado una localidad valida.");
        }

    }

    //Elegir sexo usuario
    public SexoHumano elegirSexo(Integer x) throws ErrorServicio {

        if (null == x) {
            throw new ErrorServicio("No ha elegido un sexo valido.");
        } else {
            switch (x) {
                case 1:
                    return SexoHumano.MUJER;
                case 2:
                    return SexoHumano.HOMBRE;
                default:
                    return SexoHumano.OTROS;
            }
        }

    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {

        Usuario usuario = usuarioRepositorio.buscarUsuarioPorEmail(login);

        if (usuario == null) {
            usuario = usuarioRepositorio.buscarUsuarioPorUsername(login);
        }

        if (usuario != null) {

            if (usuario.getBaja() != null) {
                return null;
            }

            List<GrantedAuthority> permisos = new ArrayList<>();

            GrantedAuthority p1 = new SimpleGrantedAuthority("ROLE_" + usuario.getPrivilegios());
            permisos.add(p1);

            ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpSession session = attr.getRequest().getSession(true);
            session.setAttribute("usuariosession", usuario);

            User user = new User(usuario.getEmail(), usuario.getClave(), permisos);
            return user;
        } else {
            return null;
        }

    }

/////Metodo para dar admin    
    @Transactional
    public void darAdmin(String idUsuario) throws ErrorServicio {

        Optional<Usuario> respuesta = usuarioRepositorio.findById(idUsuario);

        if (respuesta.isPresent()) {

            Usuario usuario = respuesta.get();
            usuario.setPrivilegios(PrivilegiosUsuario.ADMIN);
            usuarioRepositorio.save(usuario);

        } else {
            throw new ErrorServicio("El usuario al que intenta dar admin no existe.");
        }

    }

    
/////Metodo para modificar perfiles de usuarios desde la vista de un admin
    @Transactional
    public void modificarAdmin(MultipartFile archivo, String username, String idUsuario, String nombre, String edad, String numero, String apellido, String email, String idLocalidad) throws ErrorServicio {

        Localidad localidad = localidadRepositorio.getOne(idLocalidad);

        if (username == null || username.isEmpty()) {
            throw new ErrorServicio("El nombre de usuario no puede ser nulo.");
        }
        
        if (edad == null || edad.trim().isEmpty()) {
            throw new ErrorServicio("La edad no puede ser nula");
        }

        if (Integer.valueOf(edad) < 12) {
            throw new ErrorServicio("La edad no puede ser menor a doce años.");
        }

        if (numero == null || numero.trim().isEmpty()) {
            throw new ErrorServicio("El numero de telefono no puede ser nulo.");
        }
     
        if (usuarioRepositorio.buscarUsuarioPorUsername(username) != null) {
            throw new ErrorServicio("El nombre de usuario ingresado ya existe.");
        }

        if (nombre == null || nombre.isEmpty()) {
            throw new ErrorServicio("El nombre del usuario no puede ser nulo.");
        }

        if (apellido == null || apellido.isEmpty()) {
            throw new ErrorServicio("El apellido del usuario no puede ser nulo.");
        }

        if (email == null || email.isEmpty()) {
            throw new ErrorServicio("El email del usuario no puede ser nulo.");
        }

        if (usuarioRepositorio.buscarUsuarioPorEmail(email) != null) {
            throw new ErrorServicio("El email ingresado ya existe.");
        }

        if (localidad == null) {
            throw new ErrorServicio("No ha ingresado una localidad valida.");
        }

        Optional<Usuario> respuesta = usuarioRepositorio.findById(idUsuario);
        if (respuesta.isPresent()) {

            Usuario usuario = respuesta.get();
            usuario.setNombre(nombre);
            usuario.setEdad(edad);
            usuario.setNumero(numero);
            usuario.setApellido(apellido);
            usuario.setEmail(email);
            usuario.setPrivilegios(PrivilegiosUsuario.USER);
            usuario.setLocalidad(localidad);

          

            String idFoto = null;
            if (usuario.getFoto() != null) {
                idFoto = usuario.getFoto().getId();
            }

            Foto foto = fotoServicio.actualizar(idFoto, archivo);
            usuario.setFoto(foto);

            usuario.setEditado(new Date());
            usuarioRepositorio.save(usuario);
        } else {
            throw new ErrorServicio("No se encontro el usuario");
        }

    }

/////Metodo para listar usuarios
    @Transactional(readOnly = true)
    public List<Usuario> listarUsuariosRegistrados() {

        return usuarioRepositorio.listarUsuariosRegistrados();

    }

}
