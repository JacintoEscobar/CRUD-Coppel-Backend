package com.coppel.crud.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "empleado")
@NamedStoredProcedureQueries({
        @NamedStoredProcedureQuery(name = "Empleado.crearEmpleado", procedureName = "insert_empleado", parameters = {
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "nombre", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "apellido", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "puesto", type = String.class)
        }),
        @NamedStoredProcedureQuery(name = "Empleado.actualizarEmpleado", procedureName = "update_empleado", parameters = {
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "ide", type = Integer.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "n", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "ap", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "pu", type = String.class)
        })
})
public class Empleado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_empleado")
    private int idEmpleado;

    @Column(name = "nombre", nullable = false)
    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(min = 1, max = 45, message = "Longitud incorrecta para el nombre")
    private String nombre;

    @Column(name = "apellido", nullable = false)
    @NotBlank(message = "El apellido no puede estar vacío")
    @Size(min = 1, max = 45, message = "Longitud incorrecta para el apellido")
    private String apellido;

    @Column(name = "puesto", nullable = false)
    @NotBlank(message = "El puesto no puede estar vacío")
    @Size(min = 1, max = 45, message = "Longitud incorrecta para el puesto")
    private String puesto;

    @JsonIgnore
    @OneToMany(mappedBy = "empleado", cascade = CascadeType.ALL)
    private List<Poliza> polizas;
}
