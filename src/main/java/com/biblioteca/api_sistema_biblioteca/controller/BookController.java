package com.biblioteca.api_sistema_biblioteca.controller;


import com.biblioteca.api_sistema_biblioteca.dto.CreateBookRequest;
import com.biblioteca.api_sistema_biblioteca.model.Book;
import com.biblioteca.api_sistema_biblioteca.service.BookService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController                    // Le dice a Spring que esta clase expondrá endpoints HTTP
@RequestMapping("/api/books")      // Define la URL base para todos los métodos de esta clase
public class BookController {

    private final BookService bookService;

    // Spring ve este constructor y automáticamente busca el BookService y lo inyecta aquí. ¡Cero factories manuales!
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public List<Book> getAllBooks() {
        return bookService.getAllBooks();
    }

    @PostMapping // Le dice a Spring que intercepte las peticiones HTTP POST a /api/books
    public Book createBook(@RequestBody CreateBookRequest request) {
        // @RequestBody le indica a Spring que tome el JSON que viene de Postman
        // y lo transforme automáticamente en un objeto de tipo Book

        return bookService.addBook(request);
    }

}
