package com.biblioteca.api_sistema_biblioteca.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice // Intercepta todas las excepciones de los controladores
public class GlobalExceptionHandler {

    // 1. Captura los errores de validación (Jakarta Validation)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        // Extraemos cada error con su respectivo mensaje personalizado
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Validación Fallida");
        response.put("messages", errors);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // 2. Captura tus excepciones personalizadas o errores de lógica (Ej: RuntimeException)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.put("error", "Error en el Servidor");
        response.put("message", ex.getMessage()); // Captura el texto de tus .orElseThrow(...)

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<Map<String, Object>> handleMethodValidationExceptions(HandlerMethodValidationException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getValueResults().forEach(result -> {
            // Obtenemos el nombre del parámetro de forma segura
            String parameterName = result.getMethodParameter().getParameterName();
            if (parameterName == null) {
                parameterName = result.getMethodParameter().getParameter().getName();
            }

            // Si por alguna razón sigue siendo genérico (ej. arg1), usamos el tipo de dato como guía
            if (parameterName.startsWith("arg")) {
                parameterName = result.getMethodParameter().getParameter().getType().getSimpleName().toLowerCase();
            }

            String finalParameterName = parameterName;
            result.getResolvableErrors().forEach(error -> {
                errors.put(finalParameterName, error.getDefaultMessage());
            });
        });

        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Validación de Parámetros Fallida");
        response.put("messages", errors);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
