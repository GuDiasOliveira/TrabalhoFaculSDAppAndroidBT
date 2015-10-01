package br.edu.utfpr.aplicacaobluetoothsd;

import java.io.Serializable;

/**
 * Created by GustavoDias on 30/09/2015.
 */
public class Serializer implements Serializable {

    private Object object;


    public Serializer(Object object) {
        this.object = object;
    }


    public Object getObject() {
        return this.object;
    }
}
