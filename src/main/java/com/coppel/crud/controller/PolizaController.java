package com.coppel.crud.controller;

import com.coppel.crud.model.Poliza;
import com.coppel.crud.respuesta.Respuesta;
import com.coppel.crud.respuesta.RespuestaCodigo;
import com.coppel.crud.respuesta.error.Error;
import com.coppel.crud.service.PolizaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@CrossOrigin
@RequestMapping(value = "/poliza")
public class PolizaController {
    @Autowired
    private PolizaService polizaService;

    @GetMapping("/{id_poliza}")
    public ResponseEntity<?> consultarPoliza(@PathVariable(required = true, name = "id_poliza") int idPoliza) {
        try {
            Poliza poliza = polizaService.consultarPolizaById(idPoliza);

            HashMap<String, String> polizaInfo = new HashMap<>();
            polizaInfo.put("idPoliza", String.valueOf(poliza.getIdPoliza()));
            polizaInfo.put("cantidad", String.valueOf(poliza.getCantidad()));

            HashMap<String, String> empleadoInfo = new HashMap<>();
            empleadoInfo.put("nombre", poliza.getEmpleado().getNombre());
            empleadoInfo.put("apellido", poliza.getEmpleado().getApellido());

            HashMap<String, String> detalleArticulo = new HashMap<>();
            detalleArticulo.put("sku", poliza.getInventario().getSKU());
            detalleArticulo.put("nombre", poliza.getInventario().getNombre());

            HashMap<String, HashMap> respuesta = new HashMap<>();
            respuesta.put("poliza", polizaInfo);
            respuesta.put("empleado", empleadoInfo);
            respuesta.put("detalleArticulo", detalleArticulo);

            return new ResponseEntity<>(new Respuesta(RespuestaCodigo.OK, respuesta), HttpStatus.FOUND);
        } catch (Exception ex) {
            return new ResponseEntity<>(new Error(RespuestaCodigo.FAILURE, ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/crear")
    public ResponseEntity<?> crearPoliza(@RequestBody(required = true) Poliza poliza) {
        try {
            polizaService.crearPoliza(poliza);
            return new ResponseEntity<>(new Respuesta(RespuestaCodigo.OK, poliza), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(new Error(RespuestaCodigo.FAILURE, "Ha ocurrido un error en los grabados de póliza"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/actualizar/{id_poliza}")
    public ResponseEntity<?> actualizarPoliza(@PathVariable(required = true, name = "id_poliza") int idPoliza, @RequestBody(required = true) Poliza poliza) {
        try {
            polizaService.actualizarPoliza(idPoliza, poliza);
            return new ResponseEntity<>(new Respuesta(RespuestaCodigo.OK, "Se actualizó correctamente la poliza " + idPoliza), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(new Error(RespuestaCodigo.FAILURE, "Ha ocurrido un error al intentar actualizar la póliza"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/eliminar/{id_poliza}")
    public ResponseEntity<?> eliminarPoliza(@PathVariable(required = true, name = "id_poliza") int idPoliza) {
        try {
            polizaService.eliminarPoliza(idPoliza);
            return new ResponseEntity<>(new Respuesta(RespuestaCodigo.OK, "Se eliminó correctamente la poliza " + idPoliza), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(new Error(RespuestaCodigo.FAILURE, "Ha ocurrido un error al intentar eliminar la póliza"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
