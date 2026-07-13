package com.biblioteca.api_sistema_biblioteca.dto;

import com.biblioteca.api_sistema_biblioteca.model.enums.BookFormat;
import com.biblioteca.api_sistema_biblioteca.model.enums.ReadingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateBookRequest {

    @NotBlank(message = "El título no puede estar vacío.")
    @Size(max = 100, message = "El título no puede superar los 100 caracteres.")
    private String title;

    @NotBlank(message = "El autor no puede estar vacío.")
    @Size(max = 50, message = "El autor no puede superar los 50 caracteres.")
    private String author;

    @NotBlank(message = "El ISBN no puede estar vacío.")
    @Size(min = 10, max = 13, message = "El ISBN debe tener entre 10 y 13 caracteres.")
    private String isbn;

    @NotBlank(message = "El estado de lectura es obligatorio.")
    private String readingStatus;

    @NotBlank(message = "El formato es obligatorio.")
    private String format;

    private boolean available;

    @NotNull(message = "El ID del usuario es obligatorio.")
    private Long userId; // ¡La clave! Aquí nos dirán a qué usuario le pertenece el libro
}
