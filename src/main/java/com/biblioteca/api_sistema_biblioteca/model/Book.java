package com.biblioteca.api_sistema_biblioteca.model;

import com.biblioteca.api_sistema_biblioteca.model.enums.BookFormat;
import com.biblioteca.api_sistema_biblioteca.model.enums.ReadingStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity // Le dice a JPA que esta clase representa una tabla en la base de datos
@Table(name = "books") // Define el nombre exacto de la tabla en PostgreSQL
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String author;

    @Column(unique = true, length = 20) // Agrega el constraint UNIQUE a nivel de BD
    private String isbn;

    @Enumerated(EnumType.STRING) // Guarda el texto del Enum en la BD
    @Column(name = "reading_status", nullable = false)
    private ReadingStatus readingStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookFormat format;

    private boolean available;

    @ManyToOne(fetch = FetchType.LAZY) // LAZY hace que el usuario solo se cargue de la BD cuando realmente lo necesites (Buena práctica de rendimiento)
    @JoinColumn(name = "user_id", nullable = false) // Mapea la columna FK 'user_id' en la tabla books
    private User user;


}
