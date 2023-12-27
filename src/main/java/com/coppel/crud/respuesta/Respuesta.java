package com.coppel.crud.respuesta;

public class Respuesta {
    public Meta meta;
    public Object data;

    public Respuesta(String meta, String mensaje) {
        this.meta = new Meta(meta);
        this.data = new Data(mensaje);
    }

    public Respuesta(String meta, Object resultado) {
        this.meta = new Meta(meta);
        this.data = resultado;
    }
}
