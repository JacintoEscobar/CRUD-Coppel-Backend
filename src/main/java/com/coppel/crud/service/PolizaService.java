package com.coppel.crud.service;

import com.coppel.crud.model.Poliza;
import com.coppel.crud.repository.PolizaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PolizaService {
    @Autowired
    private PolizaRepository polizaRepository;

    public Poliza consultarPolizaById(int idPoliza) {
        if (polizaRepository.findById(idPoliza).isEmpty()) {
            return null;
        }
        return polizaRepository.findById(idPoliza).get();
    }

    public void actualizarPoliza(int idPoliza, Poliza poliza) {
        polizaRepository.actualizarPoliza(idPoliza, poliza.getEmpleado().getIdEmpleado(), poliza.getInventario().getSKU(), poliza.getCantidad(), poliza.getFecha());
    }

    public void crearPoliza(Poliza poliza) {
        polizaRepository.crearPoliza(poliza.getEmpleado().getIdEmpleado(), poliza.getInventario().getSKU(), poliza.getCantidad(), poliza.getFecha());
    }

    public void eliminarPoliza(int idPoliza) {
        polizaRepository.eliminarPoliza(idPoliza);
    }

    public Integer getLastPolizaId() {
        return polizaRepository.getLastPolizaId();
    }
}
