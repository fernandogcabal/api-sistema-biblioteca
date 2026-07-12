package com.biblioteca.api_sistema_biblioteca.dto;

import com.biblioteca.api_sistema_biblioteca.model.enums.BookFormat;
import com.biblioteca.api_sistema_biblioteca.model.enums.ReadingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateBookRequest {

    private String title;
    private String author;
    private String isbn;
    private ReadingStatus readingStatus;
    private BookFormat format;
    private boolean available;
    private Long userId; // ¡La clave! Aquí nos dirán a qué usuario le pertenece el libro
}
