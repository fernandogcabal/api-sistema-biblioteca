package com.biblioteca.api_sistema_biblioteca.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. Captura los errores de validación en DTOs (RequestBody)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        // Cambiado a Map<String, List<String>> para acumular múltiples errores por campo
        Map<String, List<String>> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();

            errors.computeIfAbsent(fieldName, k -> new ArrayList<>()).add(errorMessage);
        });

        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Validación Fallida");
        response.put("messages", errors);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // 2. Captura errores de lógica de negocio o runtime
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.put("error", "Error en el Servidor");
        response.put("message", ex.getMessage());

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // 3. Captura errores de validación en parámetros sueltos (GET RequestParams / PathVariables)
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<Map<String, Object>> handleMethodValidationExceptions(HandlerMethodValidationException ex) {
        Map<String, List<String>> errors = new HashMap<>();
        AtomicInteger indexFallback = new AtomicInteger(0);

        ex.getValueResults().forEach(result -> {
            String parameterName = null;

            // Intento A: Intentar leer el nombre definido en la anotación @RequestParam o @PathVariable
            if (result.getMethodParameter().hasParameterAnnotation(RequestParam.class)) {
                RequestParam rp = result.getMethodParameter().getParameterAnnotation(RequestParam.class);
                if (rp != null && !rp.value().isEmpty()) parameterName = rp.value();
                else if (rp != null && !rp.name().isEmpty()) parameterName = rp.name();
            } else if (result.getMethodParameter().hasParameterAnnotation(PathVariable.class)) {
                PathVariable pv = result.getMethodParameter().getParameterAnnotation(PathVariable.class);
                if (pv != null && !pv.value().isEmpty()) parameterName = pv.value();
                else if (pv != null && !pv.name().isEmpty()) parameterName = pv.name();
            }

            // Intento B: Si no se personalizó la anotación, usar el nombre del parámetro del método
            if (parameterName == null) {
                parameterName = result.getMethodParameter().getParameterName();
            }
            if (parameterName == null) {
                parameterName = result.getMethodParameter().getParameter().getName();
            }

            // Fallback robusto sugerido por el Code Review: Si sigue siendo "arg0" o "arg1", agregar un índice único
            if (parameterName == null || parameterName.startsWith("arg")) {
                String typeName = result.getMethodParameter().getParameter().getType().getSimpleName().toLowerCase();
                parameterName = typeName + "[" + indexFallback.getAndIncrement() + "]";
            }

            String finalParameterName = parameterName;

            // Acumular todos los mensajes asociados a este parámetro específico sin sobreescribir
            result.getResolvableErrors().forEach(error -> {
                errors.computeIfAbsent(finalParameterName, k -> new ArrayList<>()).add(error.getDefaultMessage());
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