package com.coppel.crud.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "inventario")
public class Inventario {
    @Id
    @Column(name = "sku")
    private String SKU;

    @Column(name = "nombre", nullable = false)
    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(min = 1, max = 45, message = "Longitud incorrecta para el nombre")
    private String nombre;

    @Column(name = "cantidad", nullable = false)
    @NotBlank(message = "La cantidad no puede estar vacía")
    @Size(min = 1, message = "Cantidad incorrecta para el nombre")
    private int cantidad;

    @JsonIgnore
    @OneToMany(mappedBy = "inventario")
    private List<Poliza> polizas;
}
