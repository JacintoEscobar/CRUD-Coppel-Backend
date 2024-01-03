package com.coppel.crud.controller;

import com.coppel.crud.model.Inventario;
import com.coppel.crud.respuesta.Respuesta;
import com.coppel.crud.respuesta.RespuestaCodigo;
import com.coppel.crud.service.InventarioService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/inventario")
public class InventarioController {
    private static final Logger logger = LoggerFactory.getLogger(InventarioController.class);
    @Autowired
    private InventarioService inventarioService;

    @GetMapping
    public ResponseEntity<?> getAllInventario() {
        List<Inventario> inventarios = inventarioService.getAllInventario();
        if (inventarios.isEmpty()) {
            return new ResponseEntity<>(new Respuesta(RespuestaCodigo.OK, "No hay inventarios registrados"), HttpStatus.OK);
        }
        return new ResponseEntity<>(new Respuesta(RespuestaCodigo.OK, inventarios), HttpStatus.OK);
    }

    @PostMapping(value = "/crear", consumes = "application/json")
    public ResponseEntity<?> nuevoInventario(@Valid @RequestBody Inventario nuevoInventario, BindingResult validacion) {
        try {
            if (validacion.hasErrors()) {
                HashMap<String, String> errores = new HashMap<>();
                validacion.getFieldErrors().forEach(error -> {
                    errores.put("ERROR " + error.hashCode(), error.getDefaultMessage());
                });
                return new ResponseEntity<>(new Respuesta(RespuestaCodigo.FAILURE, errores), HttpStatus.BAD_REQUEST);
            }
            inventarioService.crearInventario(nuevoInventario);
            return new ResponseEntity<>(new Respuesta(RespuestaCodigo.OK, nuevoInventario), HttpStatus.CREATED);
        } catch (Exception ex) {
            logger.error("Ocurrió un error al intentar crear un inventario");
            if (ex.getClass().equals(DataIntegrityViolationException.class)) {
                return new ResponseEntity<>(new Respuesta(RespuestaCodigo.FAILURE, "No puede haber dos inventarios con el mismo SKU"), HttpStatus.CONFLICT);
            }
            return new ResponseEntity<>(new Respuesta(RespuestaCodigo.FAILURE, "Ha ocurrido un error en los grabados del inventario"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/actualizar/{sku}")
    public ResponseEntity<?> actualizarEmpleado(@PathVariable(name = "sku") String sku, @Valid @RequestBody Inventario inventarioModificado, BindingResult validacion) {
        try {
            if (validacion.hasErrors()) {
                HashMap<String, String> errores = new HashMap<>();
                validacion.getFieldErrors().forEach(error -> {
                    errores.put("ERROR " + error.hashCode(), error.getDefaultMessage());
                });
                return new ResponseEntity<>(new Respuesta(RespuestaCodigo.FAILURE, errores), HttpStatus.BAD_REQUEST);
            }
            inventarioService.actualizarInventario(inventarioModificado);
            return new ResponseEntity<>(new Respuesta(RespuestaCodigo.OK, inventarioModificado), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("Ocurrió un error al intentar actualizar el inventario");
            return new ResponseEntity<>(new Respuesta(RespuestaCodigo.FAILURE, "Ha ocurrido un error al intentar actualizar el inventario"), HttpStatus.BAD_REQUEST);
        }
    }
}
