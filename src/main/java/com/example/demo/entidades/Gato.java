package com.example.demo.entidades;

import com.example.demo.enums.Raza;
import com.example.demo.enums.SexoAnimal;
import java.util.Date;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.annotations.GenericGenerator;

@Entity
public class Gato {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

    private String nombre;

    @Enumerated(EnumType.STRING)
    private Raza raza;

    private Integer anios;
    
    private Integer meses;

    @Enumerated(EnumType.STRING)
    private SexoAnimal sexo;
    
    @OneToOne
    private Foto fotoPrincipal;

    @OneToMany
    private List<Foto> fotos;

    @ManyToOne
    private Usuario usuario;

    private String descripcion;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date creado;

    @Temporal(TemporalType.TIMESTAMP)
    private Date editado;

    private boolean activo;

    public Gato() {
    }

    public Gato(String id, String nombre, Raza raza, Integer anios, Integer meses, SexoAnimal sexo, Foto fotoPrincipal, List<Foto> fotos, Usuario usuario, String descripcion, Date creado, Date editado, boolean activo) {
        this.id = id;
        this.nombre = nombre;
        this.raza = raza;
        this.anios = anios;
        this.meses = meses;
        this.sexo = sexo;
        this.fotoPrincipal = fotoPrincipal;
        this.fotos = fotos;
        this.usuario = usuario;
        this.descripcion = descripcion;
        this.creado = creado;
        this.editado = editado;
        this.activo = activo;
    }

    
    
    public Integer getAnios() {
        return anios;
    }

    public void setAnios(Integer anios) {
        this.anios = anios;
    }

    public Integer getMeses() {
        return meses;
    }

    public void setMeses(Integer meses) {
        this.meses = meses;
    }

    
    public SexoAnimal getSexo() {
        return sexo;
    }

    public void setSexo(SexoAnimal sexo) {
        this.sexo = sexo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

  
    public Foto getFotoPrincipal() {
        return fotoPrincipal;
    }

    public void setFotoPrincipal(Foto fotoPrincipal) {
        this.fotoPrincipal = fotoPrincipal;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Raza getRaza() {
        return raza;
    }

    public void setRaza(Raza raza) {
        this.raza = raza;
    }


    public List<Foto> getFotos() {
        return fotos;
    }

    public void setFotos(List<Foto> fotos) {
        this.fotos = fotos;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Date getCreado() {
        return creado;
    }

    public void setCreado(Date creado) {
        this.creado = creado;
    }

    public Date getEditado() {
        return editado;
    }

    public void setEditado(Date editado) {
        this.editado = editado;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

}