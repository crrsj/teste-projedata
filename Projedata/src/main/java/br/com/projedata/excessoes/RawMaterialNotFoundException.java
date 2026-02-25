package br.com.projedata.excessoes;

public class RawMaterialNotFoundException extends RuntimeException {

    public RawMaterialNotFoundException(String message){
        super(message);
    }
}
