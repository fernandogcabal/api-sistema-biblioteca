package com.biblioteca.api_sistema_biblioteca.model;

import com.biblioteca.api_sistema_biblioteca.model.enums.BookFormat;
import com.biblioteca.api_sistema_biblioteca.model.enums.ReadingStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate; // 💡 Importante para la auditoría
import org.springframework.data.annotation.LastModifiedDate; // 💡 Importante para la auditoría
import org.springframework.data.jpa.domain.support.AuditingEntityListener; // 💡 Importante para el listener
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class) // 💡 Activa el "oyente" de eventos de auditoría para esta tabla
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String author;

    @Column(unique = true, length = 20)
    private String isbn;

    @Enumerated(EnumType.STRING)
    @Column(name = "reading_status", nullable = false)
    private ReadingStatus readingStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookFormat format;

    private boolean available;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 💡 CAMPO 1: Fecha de Creación automática
    @CreatedDate
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    // 💡 CAMPO 2: Fecha de Última Modificación automática
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}