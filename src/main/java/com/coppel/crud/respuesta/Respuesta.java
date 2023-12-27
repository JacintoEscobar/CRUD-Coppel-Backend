package com.coppel.crud.respuesta;

public class Respuesta {
    public Meta meta;
    public Data data;

    public Respuesta(String mensaje) {
        this.meta = new Meta();
        this.data = new Data(mensaje);
    }
}
