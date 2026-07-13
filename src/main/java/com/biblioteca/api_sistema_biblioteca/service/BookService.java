package com.biblioteca.api_sistema_biblioteca.service;


import com.biblioteca.api_sistema_biblioteca.dto.CreateBookRequest;
import com.biblioteca.api_sistema_biblioteca.model.Book;
import com.biblioteca.api_sistema_biblioteca.model.User;
import com.biblioteca.api_sistema_biblioteca.repository.BookRepository;
import com.biblioteca.api_sistema_biblioteca.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service // Le dice a Spring que esta clase guarda la lógica de negocio
public class BookService {

    // Inyectamos el repositorio a través del constructor (Práctica estándar industrial)
    private final BookRepository bookRepository;
    private final UserRepository userRepository; // Inyectamos también el repositorio de usuarios

    public BookService(BookRepository bookRepository, UserRepository userRepository) {
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    // 💡 Cambiado de List<Book> a Page<Book> y ahora recibe el parámetro pageable
    public Page<Book> getAllBooks(Pageable pageable) {
        return bookRepository.findAll(pageable);
    }

    public Book addBook(CreateBookRequest request) {
        // 1. Buscamos al usuario en la BD usando el ID del DTO.
        // Si no existe, lanzamos una excepción (luego la controlaremos mejor)
        User owner = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Error: El usuario con ID " + request.getUserId() + " no existe."));

        // 2. Mapeamos los datos del DTO a la Entidad real usando el Builder de Lombok
        Book bookEntity = Book.builder()
                .title(request.getTitle())
                .author(request.getAuthor())
                .isbn(request.getIsbn())
                .readingStatus(request.getReadingStatus())
                .format(request.getFormat())
                .available(request.isAvailable())
                .user(owner) // ¡Aquí se amarra la Foreign Key en PostgreSQL!
                .build();

        // 3. Guardamos la entidad real en la base de datos
        return bookRepository.save(bookEntity);
    }

    public void deleteById(Long id){
        bookRepository.deleteById(id);
    }

    public Book updateBook(Long id, CreateBookRequest request) {
        // 1. Buscamos el libro existente en la BD
        Book existingBook = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Error: El libro con ID " + id + " no existe."));

        // 2. Buscamos al usuario dueño por si cambió (o mantenemos el mismo)
        User owner = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Error: Usuario no encontrado."));

        // 3. Actualizamos los campos de la entidad
        existingBook.setTitle(request.getTitle());
        existingBook.setAuthor(request.getAuthor());
        existingBook.setIsbn(request.getIsbn());
        existingBook.setReadingStatus(request.getReadingStatus());
        existingBook.setFormat(request.getFormat());
        existingBook.setAvailable(request.isAvailable());
        existingBook.setUser(owner);

        // 4. Guardamos los cambios
        return bookRepository.save(existingBook);
    }
}
