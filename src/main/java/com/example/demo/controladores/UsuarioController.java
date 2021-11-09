/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.demo.controladores;

import com.example.demo.entidades.Localidad;
import com.example.demo.entidades.Usuario;
import com.example.demo.excepciones.ErrorServicio;
import com.example.demo.repositorios.LocalidadRepositorio;
import com.example.demo.servicios.UsuarioServicio;
import java.util.List;
import javax.servlet.http.HttpSession;
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
@RequestMapping("/usuario")    
public class UsuarioController {
    
    @Autowired
    private UsuarioServicio usuarioServicio;
    
    @Autowired
    private LocalidadRepositorio localidadRepositorio;
    
    
    @PreAuthorize("hasAnyRole('ROLE_USUARIO_REGISTRADO')")
    @GetMapping("/editar-perfil")
    public String editarPerfil(HttpSession session, @RequestParam String id, ModelMap model){
        
        List<Localidad> zonas = localidadRepositorio.findAll();
        model.put("zonas", zonas);
        
        Usuario login = (Usuario) session.getAttribute("usuariosession");
        if (login == null || !login.getId().equals(id)) {
            return "redirect:/inicio";
        }
        
        try {
            Usuario usuario=usuarioServicio.buscarPorId(id);
            model.addAttribute("perfil",usuario);
            
            
        } catch (Exception e) {
            model.addAttribute("error",e.getMessage());
        }
        return "perfil.html";
    }
    
    @PreAuthorize("hasAnyRole('ROLE_USUARIO_REGISTRADO')")
    @PostMapping("/actualizar-perfil")
    public String actualizarPerfil(ModelMap modelo, @RequestParam String id, HttpSession session,@RequestParam String nombre, @RequestParam String apellido,
            @RequestParam String email,@RequestParam int edad, @RequestParam String clave1, @RequestParam String clave2,@RequestParam MultipartFile archivo,String idLocalidad) {
        Usuario usuario = null;

        try {
            usuario = usuarioServicio.buscarPorId(id);

            Usuario loginUsuario = (Usuario) session.getAttribute("usuariosession");
            if (loginUsuario == null || !loginUsuario.getId().equals(id)) {
                return "redirect:/inicio";
            }
            usuarioServicio.modificar(archivo, id, nombre, edad, nombre, apellido, email, clave2, clave2, idLocalidad);
            modelo.addAttribute("perfil", usuario);
            session.setAttribute("usuariosession", usuario);
            modelo.put("titulo", "Perfil Actualizado!!");
            modelo.put("descripcion", "cambios guardados con exito!!!");
            return "exito2";
        } catch (ErrorServicio e) {
            List<Localidad> localidad = localidadRepositorio.findAll();
            modelo.put("localidad", localidad);
            modelo.put("error", e.getMessage());
            modelo.put("perfil", usuario);
            return "registro.html";
        }

    }
}
