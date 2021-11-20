package com.example.demo.controladores;

import com.example.demo.entidades.Localidad;
import com.example.demo.entidades.TokenUsuario;
import com.example.demo.entidades.Usuario;
import com.example.demo.excepciones.ErrorServicio;
import com.example.demo.servicios.LocalidadServicio;
import com.example.demo.servicios.TokenServicio;
import com.example.demo.servicios.UsuarioServicio;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/usuario")
public class UsuarioControlador {

    @Autowired
    private TokenServicio tokenServicio;

    @Autowired
    private LocalidadServicio localidadServicio;

    @Autowired
    private UsuarioServicio usuarioServicio;

    //Controlador para ingresar el email del usuario que desea recuperar su contraseña.
    @GetMapping("/recuperar-password")
    public String recuperarPassword() {

        return "recuperarpass.html";
    }

    //Controlador para enviar los emails con los tokens de recuperación de contraseñas.
    @PostMapping("/email-de-recuperacion")
    public String enviarTokenPorEmail(RedirectAttributes modelo, @RequestParam String email) {

        try {

            tokenServicio.enviarTokenPorMail(email);
            modelo.addFlashAttribute("exito", "¡Genial, ya le hemos enviado un correo a su e-mail para que pueda restablecer su contraseña");
            return "redirect:/usuario/recuperar-password";

        } catch (Exception e) {

            modelo.addFlashAttribute("error", e.getMessage());
            return "redirect:/usuario/recuperar-password";
        }
    }

    //Controlador para que el usuario elija su nueva contraseña.
    @GetMapping("/elegir-nueva-password")
    public String elegirPasswordNueva(ModelMap modelo, @RequestParam String idtoken) {
        try {
            TokenUsuario token = tokenServicio.buscarTokenPorId(idtoken);
            modelo.addAttribute("idtoken", token.getId());
            return "ingresarnuevapw.html";
        } catch (Exception e) {
            modelo.addAttribute("error", e.getMessage());
            return "index.html";
        }
    }

    //Controlador para actualizar la contraseña.
    @PostMapping("/actualizar-password")
    public String actualizarPasswordUsuario(ModelMap modelo, @RequestParam String idtoken, @RequestParam String clave1, @RequestParam String clave2) {

        try {

            TokenUsuario token = tokenServicio.buscarTokenPorId(idtoken);
            tokenServicio.restablecerPasswordUsuario(token, clave1, clave2);
            modelo.addAttribute("nuevapw", "¡Su contraseña ha sido restablecida correctamente!");
            return "index.html";

        } catch (Exception e) {

            modelo.addAttribute("idtoken", idtoken);
            modelo.addAttribute("error", e.getMessage());
            return "ingresarnuevapw.html";
        }

    }

/////Falta vista del usuario para vincular con este metodo   
    @GetMapping("/editar-perfil")
    public String editarPerfil(HttpSession session, @RequestParam String id, ModelMap model) {

        Usuario login = (Usuario) session.getAttribute("usuariosession");

        if (login == null || !login.getId().equals(id)) {
            return "redirect:/inicio";
        }

        try {
            Usuario usuario = usuarioServicio.buscarUsuarioPorId(id);
            model.addAttribute("perfil", usuario);

            List<Localidad> localidades = localidadServicio.listarLocalidades();
            
            model.put("localidades", localidades);

            return "registro.html";
        } catch (ErrorServicio e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/";
        }

    }

   
    @PostMapping("/actualizar-perfil")
    public String actualizarPerfil(ModelMap modelo, @RequestParam String id, HttpSession session, @RequestParam String nombre, @RequestParam String apellido,
            @RequestParam String email, @RequestParam String edad, @RequestParam String clave1, @RequestParam String clave2, @RequestParam MultipartFile archivo,
            String idLocalidad, @RequestParam String username, @RequestParam Integer sexo, @RequestParam String numero) {
        
        Usuario usuario = null;
 
        try {
            usuario = usuarioServicio.buscarUsuarioPorId(id);

            Usuario loginUsuario = (Usuario) session.getAttribute("usuariosession");

            if (loginUsuario == null || !loginUsuario.getId().equals(id)) {
                return "redirect:/inicio";
            }

            usuarioServicio.modificar(archivo, username, id, nombre, sexo, edad, numero, apellido, email, clave1, clave2, idLocalidad);
            
            session.setAttribute("usuariosession", usuario);
            modelo.put("titulo", "Perfil Actualizado!!");
            modelo.put("descripcion", "cambios guardados con exito!!!");
            return "exito";
        } catch (ErrorServicio e) {
            List<Localidad> localidades = localidadServicio.listarLocalidades();
            modelo.put("localidad", localidades);
            modelo.put("error", e.getMessage());
            modelo.put("perfil", usuario);
            return "registro.html";
        }

    }
}
