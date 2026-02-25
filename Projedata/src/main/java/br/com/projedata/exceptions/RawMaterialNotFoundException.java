package br.com.projedata.exceptions;

public class RawMaterialNotFoundException extends RuntimeException {

    public RawMaterialNotFoundException(String message){
        super(message);
    }
}
