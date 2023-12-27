package com.coppel.crud.repository;

import com.coppel.crud.model.Poliza;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.stereotype.Repository;

@Repository
public interface PolizaRepository extends JpaRepository<Poliza, Integer> {
    @Procedure
    void actualizarPoliza(int idp, int eg, String s, int ca, String f);
}
