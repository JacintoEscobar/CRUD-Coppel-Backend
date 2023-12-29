package com.coppel.crud.repository;

import com.coppel.crud.model.Inventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventarioRepository extends JpaRepository<Inventario, String> {
    List<Inventario> findByOrderBySKUAsc();

    @Procedure
    void crearInventario(String s, String n, int ca);

    @Procedure
    void actualizarInventario(String s, String n, int ca);
}
