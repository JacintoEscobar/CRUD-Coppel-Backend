package com.coppel.crud.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity
@Data
@Table(name = "poliza")
@NamedStoredProcedureQuery(name = "Poliza.actualizarPoliza", procedureName = "update_poliza", parameters = {@StoredProcedureParameter(mode = ParameterMode.IN, name = "idp", type = Integer.class), @StoredProcedureParameter(mode = ParameterMode.IN, name = "eg", type = Integer.class), @StoredProcedureParameter(mode = ParameterMode.IN, name = "s", type = String.class), @StoredProcedureParameter(mode = ParameterMode.IN, name = "ca", type = Integer.class), @StoredProcedureParameter(mode = ParameterMode.IN, name = "f", type = String.class)})
public class Poliza {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idPoliza;

    @ManyToOne
    @JoinColumn(name = "empleado_genero")
    private Empleado empleado;

    @ManyToOne
    @JoinColumn(name = "sku")
    private Inventario inventario;

    @Column(name = "cantidad", nullable = false)
    @NotBlank(message = "La cantidad no puede estar vacía")
    @Size(min = 1, message = "Cantidad incorrecta para la poliza")
    private int cantidad;

    @Column(name = "fecha", nullable = false)
    @NotBlank(message = "Fecha incorecta")
    private String fecha;
}