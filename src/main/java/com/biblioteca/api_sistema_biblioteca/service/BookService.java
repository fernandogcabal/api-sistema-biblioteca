package com.biblioteca.api_sistema_biblioteca.service;

import com.biblioteca.api_sistema_biblioteca.dto.CreateBookRequest;
import com.biblioteca.api_sistema_biblioteca.model.Book;
import com.biblioteca.api_sistema_biblioteca.model.User;
import com.biblioteca.api_sistema_biblioteca.model.enums.BookFormat;       // 💡 Importación añadida
import com.biblioteca.api_sistema_biblioteca.model.enums.ReadingStatus;    // 💡 Importación añadida
import com.biblioteca.api_sistema_biblioteca.repository.BookRepository;
import com.biblioteca.api_sistema_biblioteca.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public BookService(BookRepository bookRepository, UserRepository userRepository) {
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    // 💡 Modificamos el mét0do para que reciba un parámetro opcional 'title'
    public Page<Book> getAllBooks(String title, Pageable pageable) {
        String normalizedTitle = title == null ? null : title.trim();
        if (normalizedTitle != null && !normalizedTitle.trim().isEmpty()) {
            return bookRepository.findByTitleContainingIgnoreCase(normalizedTitle, pageable);
        }
        return bookRepository.findAll(pageable);
    }

    public Book addBook(CreateBookRequest request) {
        User owner = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Error: El usuario con ID " + request.getUserId() + " no existe."));

        // 💡 Corregido usando .valueOf() para convertir de String a Enum
        Book bookEntity = Book.builder()
                .title(request.getTitle())
                .author(request.getAuthor())
                .isbn(request.getIsbn())
                .readingStatus(ReadingStatus.valueOf(request.getReadingStatus()))
                .format(BookFormat.valueOf(request.getFormat()))
                .available(request.isAvailable())
                .user(owner)
                .build();

        return bookRepository.save(bookEntity);
    }

    public void deleteById(Long id){
        bookRepository.deleteById(id);
    }

    public Book updateBook(Long id, CreateBookRequest request) {
        Book existingBook = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Error: El libro con ID " + id + " no existe."));

        User owner = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Error: Usuario no encontrado."));

        existingBook.setTitle(request.getTitle());
        existingBook.setAuthor(request.getAuthor());
        existingBook.setIsbn(request.getIsbn());
        existingBook.setReadingStatus(ReadingStatus.valueOf(request.getReadingStatus())); // Converte String -> Enum
        existingBook.setFormat(BookFormat.valueOf(request.getFormat()));                 // Converte String -> Enum
        existingBook.setAvailable(request.isAvailable());
        existingBook.setUser(owner);

        return bookRepository.save(existingBook);
    }
}