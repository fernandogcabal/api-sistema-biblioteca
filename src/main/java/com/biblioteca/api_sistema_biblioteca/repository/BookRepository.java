package com.biblioteca.api_sistema_biblioteca.repository;

import com.biblioteca.api_sistema_biblioteca.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository // Le dice a Spring que esta clase maneja los datos (Inyección de Dependencias)
public interface BookRepository extends JpaRepository<Book, Long> {
    // Al extender de JpaRepository<Entidad, TipoDeID>, Spring hereda automáticamente:
    // .findAll(), .save(), .findById(), .deleteById() y mucho más.
    // ¡No necesitas escribir código aquí!
}
