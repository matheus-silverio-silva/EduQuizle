package br.com.eduquizle.api.exception;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex) {
        Map<String, String> response = Map.of("erro", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST); // Retorna status 400 Bad Request
    }
}