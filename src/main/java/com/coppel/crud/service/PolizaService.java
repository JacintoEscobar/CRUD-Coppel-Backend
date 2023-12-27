package com.coppel.crud.service;

import com.coppel.crud.model.Poliza;
import com.coppel.crud.repository.PolizaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PolizaService {
    @Autowired
    private PolizaRepository polizaRepository;

    public void actualizarPoliza(int idPoliza, Poliza poliza) {
        polizaRepository.actualizarPoliza(idPoliza, poliza.getEmpleado().getIdEmpleado(), poliza.getInventario().getSKU(), poliza.getCantidad(), poliza.getFecha());
    }
}