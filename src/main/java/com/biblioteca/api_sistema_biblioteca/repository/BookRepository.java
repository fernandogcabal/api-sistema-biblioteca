package com.biblioteca.api_sistema_biblioteca.repository;

import com.biblioteca.api_sistema_biblioteca.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    // 💡 Busca libros donde el título contenga la palabra ingresada (ignora mayúsculas/minúsculas)
    Page<Book> findByTitleContainingIgnoreCase(String title, Pageable pageable);
}
