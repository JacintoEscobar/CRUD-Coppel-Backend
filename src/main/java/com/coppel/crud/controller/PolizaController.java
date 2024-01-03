package com.coppel.crud.controller;

import com.coppel.crud.model.Poliza;
import com.coppel.crud.respuesta.Respuesta;
import com.coppel.crud.respuesta.RespuestaCodigo;
import com.coppel.crud.service.PolizaService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping(value = "/poliza")
public class PolizaController {
    private static final Logger logger = LoggerFactory.getLogger(PolizaController.class);
    @Autowired
    private PolizaService polizaService;

    @GetMapping
    public ResponseEntity<?> getAllPoliza() {
        List<Poliza> polizas = polizaService.getAllPoliza();
        if (polizas.isEmpty()) {
            return new ResponseEntity<>(new Respuesta(RespuestaCodigo.OK, "No hay pólizas registradas"), HttpStatus.OK);
        }
        return new ResponseEntity<>(new Respuesta(RespuestaCodigo.OK, polizas), HttpStatus.OK);
    }

    @GetMapping("/{id_poliza}")
    public ResponseEntity<?> consultarPoliza(@PathVariable(name = "id_poliza") int idPoliza) {
        try {
            Poliza polizaExistente = polizaService.consultarPolizaById(idPoliza);
            if (polizaExistente == null) {
                throw new Exception("No existe la póliza con el id " + idPoliza);
            }

            return new ResponseEntity<>(new Respuesta(RespuestaCodigo.OK, crearRespuestaPoliza(polizaExistente)), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("Ocurrió un error al intentar consultar la póliza espeficada");
            return new ResponseEntity<>(new Respuesta(RespuestaCodigo.FAILURE, "Ha ocurrido un error al consultar la póliza"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/crear", consumes = "application/json")
    public ResponseEntity<?> crearPoliza(@Valid @RequestBody Poliza nuevaPoliza, BindingResult validacion) {
        try {
            if (validacion.hasErrors()) {
                HashMap<String, String> errores = new HashMap<>();
                validacion.getFieldErrors().forEach(error -> {
                    errores.put("ERROR " + error.hashCode(), error.getDefaultMessage());
                });
                return new ResponseEntity<>(new Respuesta(RespuestaCodigo.FAILURE, errores), HttpStatus.BAD_REQUEST);
            }
            polizaService.crearPoliza(nuevaPoliza);

            Poliza polizaRecienCreada = polizaService.consultarPolizaById(polizaService.getLastPolizaId());
            return new ResponseEntity<>(new Respuesta(RespuestaCodigo.OK, crearRespuestaPoliza(polizaRecienCreada)), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("Ocurrió un error al intentar crear la póliza");
            return new ResponseEntity<>(new Respuesta(RespuestaCodigo.FAILURE, "Ha ocurrido un error en los grabados de póliza"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/actualizar/{id_poliza}")
    public ResponseEntity<?> actualizarPoliza(@PathVariable(name = "id_poliza") int idPoliza, @Valid @RequestBody Poliza polizaModificada, BindingResult validacion) {
        try {
            if (validacion.hasErrors()) {
                HashMap<String, String> errores = new HashMap<>();
                validacion.getFieldErrors().forEach(error -> {
                    errores.put("ERROR " + error.hashCode(), error.getDefaultMessage());
                });
                return new ResponseEntity<>(new Respuesta(RespuestaCodigo.FAILURE, errores), HttpStatus.BAD_REQUEST);
            }

            Poliza polizaExistente = polizaService.consultarPolizaById(idPoliza);
            if (polizaExistente == null) {
                return new ResponseEntity<>(new Respuesta(RespuestaCodigo.FAILURE, "No existe la póliza " + idPoliza), HttpStatus.NOT_FOUND);
            }

            polizaService.actualizarPoliza(idPoliza, polizaModificada);
            return new ResponseEntity<>(new Respuesta(RespuestaCodigo.OK, "Se actualizó correctamente la poliza " + idPoliza), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("Ocurrió un error al intentar actualizar la póliza");
            return new ResponseEntity<>(new Respuesta(RespuestaCodigo.FAILURE, "Ha ocurrido un error al intentar actualizar la póliza"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/eliminar/{id_poliza}")
    public ResponseEntity<?> eliminarPoliza(@PathVariable(name = "id_poliza") int idPoliza) {
        try {
            Poliza polizaExistente = polizaService.consultarPolizaById(idPoliza);
            if (polizaExistente == null) {
                return new ResponseEntity<>(new Respuesta(RespuestaCodigo.FAILURE, "No existe la póliza " + idPoliza), HttpStatus.NOT_FOUND);
            }

            polizaService.eliminarPoliza(idPoliza);
            return new ResponseEntity<>(new Respuesta(RespuestaCodigo.OK, "Se eliminó correctamente la poliza " + idPoliza), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("Ocurrió un error al intentar eliminar la póliza");
            return new ResponseEntity<>(new Respuesta(RespuestaCodigo.FAILURE, "Ha ocurrido un error al intentar eliminar la póliza"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private HashMap<String, HashMap> crearRespuestaPoliza(Poliza poliza) {
        HashMap<String, Integer> polizaInfo = new HashMap<>();
        polizaInfo.put("idPoliza", poliza.getIdPoliza());
        polizaInfo.put("cantidad", poliza.getCantidad());

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

        return respuesta;
    }
}
