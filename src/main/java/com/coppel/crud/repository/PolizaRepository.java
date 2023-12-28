package com.coppel.crud.repository;

import com.coppel.crud.model.Poliza;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.stereotype.Repository;

@Repository
public interface PolizaRepository extends JpaRepository<Poliza, Integer> {
    @Procedure
    void actualizarPoliza(int idp, int eg, String s, int ca, String f);

    @Procedure("insert_poliza")
    void crearPoliza(int eg, String s, int ca, String f);

    @Procedure
    void eliminarPoliza(int idp);

    @Query(value = "SELECT get_last_poliza_id()", nativeQuery = true)
    Integer getLastPolizaId();
}
