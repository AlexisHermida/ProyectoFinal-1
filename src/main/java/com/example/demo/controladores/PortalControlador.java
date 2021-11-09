
package com.example.demo.controladores;

import com.example.demo.entidades.Localidad;
import com.example.demo.excepciones.ErrorServicio;

import com.example.demo.repositorios.LocalidadRepositorio;
import com.example.demo.servicios.UsuarioServicio;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/")
public class PortalControlador {

    @Autowired
    private UsuarioServicio usuarioServicio;

    @Autowired
    private LocalidadRepositorio localidadRepositorio;

    @GetMapping("/")
    public String index() {
        return "index.html";
    }

    
    @PreAuthorize("hasAnyRole('ROLE_USUARIO_REGISTRADO')")
    @GetMapping("/inicio")
    public String inicio() {
        return "inicio.html";
    }
    
     

    @GetMapping("/login")
    public String login(@RequestParam(required = false)String error,@RequestParam(required = false) String logout, ModelMap model) {
        
        if (error!=null) {
            model.put("error", "Nombre de usuario o clave incorrectas");
        }
        if (logout!=null) {
            model.put("logout", "Ha salido correctamente");
        }
        
        return "login.html";
    }

    @GetMapping("/registro")
    public String registro(ModelMap modelo) {
        List<Localidad> localidad = localidadRepositorio.findAll();
        modelo.put("localidad", localidad);
        return "registro.html";
    }

    @GetMapping("/exito")
    public String exito() {
        return "exito.html";
    }
    
    @GetMapping("/exito2")
    public String exito2() {
        return "exito2.html";
    }

    @PostMapping("/registrar")
    public String registrar(ModelMap modelo, MultipartFile archivo, @RequestParam String nombre, @RequestParam int edad,@RequestParam String numero,@RequestParam String apellido, @RequestParam String email, @RequestParam String clave1, @RequestParam String clave2, @RequestParam String localidadId) {

        try {
            usuarioServicio.registrar(archivo, nombre, edad, numero, apellido, email, clave1, clave2, email);
        } catch (ErrorServicio ex) {
            List<Localidad> localidad = localidadRepositorio.findAll();
            modelo.put("localidad", localidad);
            modelo.put("error", ex.getMessage());
            modelo.put("nombre", nombre);
            modelo.put("apellido", apellido);
            modelo.put("email", email);
            modelo.put("clave1", clave1);
            modelo.put("clave2", clave2);
            return "registro.html";
        }
        modelo.put("titulo", "Bienvenidos Patitas");//Insertar nombre pagina
        modelo.put("descripcion", "Se a registrado con exito");
        return "exito.html";
    }

}

 

