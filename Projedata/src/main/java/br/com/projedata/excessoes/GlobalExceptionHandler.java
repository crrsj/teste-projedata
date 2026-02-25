package br.com.projedata.excessoes;

import br.com.projedata.dto.ValidateFieldsDTO;
import br.com.projedata.dto.ErrorMessage;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorMessage> handleProductNotFound(ProductNotFoundException ex, HttpServletRequest requisicao) {

        ErrorMessage error = new ErrorMessage(
                Instant.now(),
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                requisicao.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }


    @ExceptionHandler(RawMaterialNotFoundException.class)
    public ResponseEntity<ErrorMessage>handleRawMaterialNotFound  (RawMaterialNotFoundException ex, HttpServletRequest requisicao ){
        ErrorMessage error = new ErrorMessage(
                Instant.now(),
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                requisicao.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<?> HandleValidationErrors(MethodArgumentNotValidException ex) {
            var erros = ex.getFieldErrors();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erros.stream().map(ValidateFieldsDTO::new).toList());
        }


}