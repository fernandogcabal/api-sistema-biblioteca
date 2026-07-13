package com.biblioteca.api_sistema_biblioteca.controller;


import com.biblioteca.api_sistema_biblioteca.dto.CreateBookRequest;
import com.biblioteca.api_sistema_biblioteca.model.Book;
import com.biblioteca.api_sistema_biblioteca.service.BookService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@RestController                    // Le dice a Spring que esta clase expondrá endpoints HTTP
@RequestMapping("/api/books")      // Define la URL base para todos los métodos de esta clase
public class BookController {

    private final BookService bookService;

    // Spring ve este constructor y automáticamente busca el BookService y lo inyecta aquí. ¡Cero factories manuales!
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public Page<Book> getAllBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        // Creamos el objeto de paginación que entiende Spring Data
        Pageable pageable = PageRequest.of(page, size);
        return bookService.getAllBooks(pageable);
    }

    @PostMapping // Le dice a Spring que intercepte las peticiones HTTP POST a /api/books
    public Book createBook(@RequestBody CreateBookRequest request) {
        // @RequestBody le indica a Spring que tome el JSON que viene de Postman
        // y lo transforme automáticamente en un objeto de tipo Book

        return bookService.addBook(request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        // Tu lógica para borrar de la base de datos usando el repositorio:
        bookService.deleteById(id);
        return ResponseEntity.noContent().build(); // Retorna un estado 204 sin contenido (éxito)
    }

    @PutMapping("/{id}")
    public ResponseEntity<Book> updateBook(@PathVariable Long id, @RequestBody CreateBookRequest request) {
        // Llamamos al servicio para actualizar y retornamos el libro modificado
        Book updatedBook = bookService.updateBook(id, request);
        return ResponseEntity.ok(updatedBook);
    }
}
