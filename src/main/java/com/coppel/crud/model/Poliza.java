package com.coppel.crud.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Data
@Table(name = "poliza")
@NamedStoredProcedureQueries({
        @NamedStoredProcedureQuery(name = "Poliza.crearPoliza", procedureName = "insert_poliza", parameters = {
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "eg", type = Integer.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "s", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "ca", type = Integer.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "f", type = String.class)
        }),
        @NamedStoredProcedureQuery(name = "Poliza.actualizarPoliza", procedureName = "update_poliza", parameters = {
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "idp", type = Integer.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "eg", type = Integer.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "s", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "ca", type = Integer.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "f", type = String.class)
        }),
        @NamedStoredProcedureQuery(name = "Poliza.eliminarPoliza", procedureName = "delete_poliza", parameters = {
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "idp", type = Integer.class)
        }),
        @NamedStoredProcedureQuery(name = "Poliza.getLastPolizaId", procedureName = "get_last_poliza_id"),
        @NamedStoredProcedureQuery(name = "Poliza.actualizarCamposPoliza", procedureName = "update_campos_poliza", parameters = {
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "idp", type = Integer.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "ide", type = Integer.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "s", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "ca", type = Integer.class),
        })
})
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
    @NotNull(message = "La cantidad no puede estar vacía")
    @Min(value = 1, message = "La cantidad de la póliza debe ser mayor a 0")
    private int cantidad;

    @Column(name = "fecha", nullable = false)
    @NotBlank(message = "Fecha incorecta")
    private String fecha;
}