package com.coppel.crud.service;

import com.coppel.crud.model.Empleado;
import com.coppel.crud.repository.EmpleadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmpleadoService {
    @Autowired
    private EmpleadoRepository empleadoRepository;

    public List<Empleado> getAllEmpleado() {
        return empleadoRepository.findByOrderByIdEmpleadoAsc();
    }

    public void crearEmpleado(Empleado nuevoEmpleado) {
        empleadoRepository.crearEmpleado(nuevoEmpleado.getNombre(), nuevoEmpleado.getApellido(), nuevoEmpleado.getPuesto());
    }

    public void actualizarEmpleado(Empleado empleadoModificado) {
        empleadoRepository.actualizarEmpleado(empleadoModificado.getIdEmpleado(), empleadoModificado.getNombre(), empleadoModificado.getApellido(), empleadoModificado.getPuesto());
    }
}
