package com.coppel.crud.repository;

import com.coppel.crud.model.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmpleadoRepository extends JpaRepository<Empleado, Integer> {
    List<Empleado> findByOrderByIdEmpleadoAsc();

    @Procedure
    void crearEmpleado(String nombre, String apellido, String puesto);

    @Procedure
    void actualizarEmpleado(int ide, String n, String ap, String pu);

    @Query(value = "SELECT get_last_empleado_id()", nativeQuery = true)
    Integer getLastEmpleadoId();
}
