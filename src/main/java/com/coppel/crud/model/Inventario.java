package com.coppel.crud.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "inventario")
@NamedStoredProcedureQueries({
        @NamedStoredProcedureQuery(name = "Inventario.crearInventario", procedureName = "insert_inventario", parameters = {
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "s", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "n", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "ca", type = Integer.class)
        }),
        @NamedStoredProcedureQuery(name = "Inventario.actualizarInventario", procedureName = "update_inventario", parameters = {
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "s", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "n", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "ca", type = Integer.class)
        })
})
public class Inventario {
    @Id
    @Column(name = "sku")
    @NotBlank(message = "El SKU no puede estar en blanco")
    @Size(min = 1, max = 45, message = "Longitud incorrecta para el SKU")
    private String SKU;

    @Column(name = "nombre", nullable = false)
    @NotBlank(message = "El nombre no puede estar en blanco")
    @Size(min = 1, max = 45, message = "Longitud incorrecta para el nombre")
    private String nombre;

    @Column(name = "cantidad", nullable = false)
    @NotNull(message = "La cantidad no puede estar vacía")
    @Min(value = 1, message = "La cantidad debe ser mayo a 0")
    private int cantidad;

    @JsonIgnore
    @OneToMany(mappedBy = "inventario")
    private List<Poliza> polizas;
}
