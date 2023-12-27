package com.coppel.crud.respuesta.error;

import com.coppel.crud.respuesta.Meta;

public class Error {
    public Meta meta;
    public Data data;

    public Error(String meta, String mensaje) {
        this.meta = new Meta(meta);
        this.data = new Data(mensaje);
    }
}
