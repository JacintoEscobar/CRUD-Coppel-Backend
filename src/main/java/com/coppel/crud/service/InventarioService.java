package com.coppel.crud.service;

import com.coppel.crud.model.Inventario;
import com.coppel.crud.repository.InventarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventarioService {
    @Autowired
    private InventarioRepository inventarioRepository;

    public List<Inventario> getAllInventario() {
        return inventarioRepository.findByOrderBySKUAsc();
    }

    public void crearInventario(Inventario inventario) {
        inventarioRepository.crearInventario(inventario.getSKU(), inventario.getNombre(), inventario.getCantidad());
    }

    public void actualizarInventario(Inventario inventarioModificado) {
        inventarioRepository.actualizarInventario(inventarioModificado.getSKU(), inventarioModificado.getNombre(), inventarioModificado.getCantidad());
    }
}
