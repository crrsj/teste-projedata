package br.com.projedata.dto;

import org.springframework.validation.FieldError;

public record ValidateFieldsDTO(String field, String message) {
    public ValidateFieldsDTO(FieldError errors){
        this(errors.getField(),errors.getDefaultMessage());
    }
}
