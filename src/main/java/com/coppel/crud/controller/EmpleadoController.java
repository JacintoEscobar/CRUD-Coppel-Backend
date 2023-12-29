package com.coppel.crud.controller;

import com.coppel.crud.model.Empleado;
import com.coppel.crud.respuesta.Respuesta;
import com.coppel.crud.respuesta.RespuestaCodigo;
import com.coppel.crud.service.EmpleadoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/empleado")
public class EmpleadoController {
    @Autowired
    private EmpleadoService empleadoService;

    @GetMapping
    public ResponseEntity<?> getAllEmpleado() {
        List<Empleado> empleados = empleadoService.getAllEmpleado();
        if (empleados.isEmpty()) {
            return new ResponseEntity<>(new Respuesta(RespuestaCodigo.OK, "No hay empleados registrados"), HttpStatus.OK);
        }
        return new ResponseEntity<>(new Respuesta(RespuestaCodigo.OK, empleados), HttpStatus.OK);
    }

    @PostMapping("/crear")
    public ResponseEntity<?> nuevoEmpleado(@Valid @RequestBody Empleado nuevoEmpleado, BindingResult validacion) {
        try {
            if (validacion.hasErrors()) {
                HashMap<String, String> errores = new HashMap<>();
                validacion.getFieldErrors().forEach(error -> {
                    errores.put("ERROR " + error.hashCode(), error.getDefaultMessage());
                });
                return new ResponseEntity<>(new Respuesta(RespuestaCodigo.FAILURE, errores), HttpStatus.BAD_REQUEST);
            }
            empleadoService.crearEmpleado(nuevoEmpleado);
            return new ResponseEntity<>(new Respuesta(RespuestaCodigo.OK, nuevoEmpleado), HttpStatus.CREATED);
        } catch (Exception ex) {
            return new ResponseEntity<>(new Respuesta(RespuestaCodigo.FAILURE, "Ha ocurrido un error en los grabados del empleado"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/actualizar/{id_empleado}")
    public ResponseEntity<?> actualizarEmpleado(@PathVariable(name = "id_empleado") int idEmpleado, @Valid @RequestBody Empleado empleadoModificado, BindingResult validacion) {
        try {
            if (validacion.hasErrors()) {
                HashMap<String, String> errores = new HashMap<>();
                validacion.getFieldErrors().forEach(error -> {
                    errores.put("ERROR " + error.hashCode(), error.getDefaultMessage());
                });
                return new ResponseEntity<>(new Respuesta(RespuestaCodigo.FAILURE, errores), HttpStatus.BAD_REQUEST);
            }
            empleadoService.actualizarEmpleado(empleadoModificado);
            return new ResponseEntity<>(new Respuesta(RespuestaCodigo.OK, empleadoModificado), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(new Respuesta(RespuestaCodigo.FAILURE, "Ha ocurrido un error al intentar actualizar el empleado"), HttpStatus.BAD_REQUEST);
        }
    }
}
