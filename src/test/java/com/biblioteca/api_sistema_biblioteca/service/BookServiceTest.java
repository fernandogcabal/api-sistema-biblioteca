package com.biblioteca.api_sistema_biblioteca.service;

import com.biblioteca.api_sistema_biblioteca.dto.CreateBookRequest;
import com.biblioteca.api_sistema_biblioteca.model.Book;
import com.biblioteca.api_sistema_biblioteca.model.User;
import com.biblioteca.api_sistema_biblioteca.model.enums.BookFormat;
import com.biblioteca.api_sistema_biblioteca.model.enums.ReadingStatus;
import com.biblioteca.api_sistema_biblioteca.repository.BookRepository;
import com.biblioteca.api_sistema_biblioteca.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BookService bookService;

    private User testUser;
    private Book testBook;
    private CreateBookRequest createBookRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .passwordHash("$2a$10$hashedpassword")
                .build();

        testBook = Book.builder()
                .id(1L)
                .title("Test Book")
                .author("Test Author")
                .isbn("978-3-16-148410-0")
                .readingStatus(ReadingStatus.READING)
                .format(BookFormat.PHYSICAL)
                .available(true)
                .user(testUser)
                .build();

        createBookRequest = new CreateBookRequest();
        createBookRequest.setTitle("Test Book");
        createBookRequest.setAuthor("Test Author");
        createBookRequest.setIsbn("978-3-16-148410-0");
        createBookRequest.setReadingStatus(ReadingStatus.READING);
        createBookRequest.setFormat(BookFormat.PHYSICAL);
        createBookRequest.setAvailable(true);
        createBookRequest.setUserId(1L);
    }

    @Test
    void testGetAllBooks_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Book> bookPage = new PageImpl<>(Arrays.asList(testBook), pageable, 1);

        when(bookRepository.findAll(pageable)).thenReturn(bookPage);

        Page<Book> result = bookService.getAllBooks(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Test Book", result.getContent().get(0).getTitle());
        verify(bookRepository, times(1)).findAll(pageable);
    }

    @Test
    void testGetAllBooks_EmptyPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Book> emptyPage = new PageImpl<>(Arrays.asList(), pageable, 0);

        when(bookRepository.findAll(pageable)).thenReturn(emptyPage);

        Page<Book> result = bookService.getAllBooks(pageable);

        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        verify(bookRepository, times(1)).findAll(pageable);
    }

    @Test
    void testAddBook_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);

        Book result = bookService.addBook(createBookRequest);

        assertNotNull(result);
        assertEquals("Test Book", result.getTitle());
        assertEquals("Test Author", result.getAuthor());
        assertEquals("978-3-16-148410-0", result.getIsbn());
        assertEquals(testUser.getId(), result.getUser().getId());
        verify(userRepository, times(1)).findById(1L);
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    void testAddBook_UserNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());
        createBookRequest.setUserId(999L);

        assertThrows(RuntimeException.class, () -> bookService.addBook(createBookRequest),
                "Error: El usuario con ID");
        
        verify(userRepository, times(1)).findById(999L);
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void testDeleteBook_Success() {
        bookService.deleteById(1L);

        verify(bookRepository, times(1)).deleteById(1L);
    }

    @Test
    void testUpdateBook_Success() {
        CreateBookRequest updateRequest = new CreateBookRequest();
        updateRequest.setTitle("Updated Title");
        updateRequest.setAuthor("Updated Author");
        updateRequest.setIsbn("978-0-12-345678-9");
        updateRequest.setReadingStatus(ReadingStatus.COMPLETED);
        updateRequest.setFormat(BookFormat.EBOOK);
        updateRequest.setAvailable(false);
        updateRequest.setUserId(1L);

        Book updatedBook = Book.builder()
                .id(1L)
                .title("Updated Title")
                .author("Updated Author")
                .isbn("978-0-12-345678-9")
                .readingStatus(ReadingStatus.COMPLETED)
                .format(BookFormat.EBOOK)
                .available(false)
                .user(testUser)
                .build();

        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(bookRepository.save(any(Book.class))).thenReturn(updatedBook);

        Book result = bookService.updateBook(1L, updateRequest);

        assertNotNull(result);
        assertEquals("Updated Title", result.getTitle());
        assertEquals("Updated Author", result.getAuthor());
        assertEquals("978-0-12-345678-9", result.getIsbn());
        assertEquals(ReadingStatus.COMPLETED, result.getReadingStatus());
        assertEquals(BookFormat.EBOOK, result.getFormat());
        assertFalse(result.isAvailable());
        verify(bookRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).findById(1L);
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    void testUpdateBook_BookNotFound() {
        when(bookRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> bookService.updateBook(999L, createBookRequest),
                "Error: El libro con ID");
        
        verify(bookRepository, times(1)).findById(999L);
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void testUpdateBook_UserNotFound() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(userRepository.findById(999L)).thenReturn(Optional.empty());
        createBookRequest.setUserId(999L);

        assertThrows(RuntimeException.class, () -> bookService.updateBook(1L, createBookRequest),
                "Error: Usuario no encontrado");
        
        verify(bookRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).findById(999L);
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void testAddBook_MultipleBooks() {
        Book secondBook = Book.builder()
                .id(2L)
                .title("Second Book")
                .author("Second Author")
                .isbn("978-3-16-148410-1")
                .readingStatus(ReadingStatus.TO_READ)
                .format(BookFormat.EBOOK)
                .available(true)
                .user(testUser)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(bookRepository.save(any(Book.class)))
                .thenReturn(testBook)
                .thenReturn(secondBook);

        Book result1 = bookService.addBook(createBookRequest);
        createBookRequest.setTitle("Second Book");
        createBookRequest.setAuthor("Second Author");
        createBookRequest.setIsbn("978-3-16-148410-1");
        Book result2 = bookService.addBook(createBookRequest);

        assertNotNull(result1);
        assertNotNull(result2);
        assertEquals(1L, result1.getId());
        assertEquals(2L, result2.getId());
        verify(bookRepository, times(2)).save(any(Book.class));
    }
}
