package com.coppel.crud.controller;

import com.coppel.crud.model.Poliza;
import com.coppel.crud.respuesta.Respuesta;
import com.coppel.crud.respuesta.error.Error;
import com.coppel.crud.service.PolizaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping(value = "/poliza")
public class PolizaController {
    @Autowired
    private PolizaService polizaService;

    @PutMapping("/actualizar/{id_poliza}")
    public ResponseEntity<?> actualizarPoliza(@PathVariable(required = true, name = "id_poliza") int idPoliza, @RequestBody(required = true) Poliza poliza) {
        try {
            polizaService.actualizarPoliza(idPoliza, poliza);
            return new ResponseEntity<>(new Respuesta("Se actualizó correctamente la poliza " + idPoliza), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(new Error("FAILURE", "Ha ocurrido un error al intentar actualizar la póliza"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
