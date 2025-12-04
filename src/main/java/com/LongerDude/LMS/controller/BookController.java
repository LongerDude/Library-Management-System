package com.LongerDude.LMS.controller;

import com.LongerDude.LMS.model.Book;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import com.LongerDude.LMS.repository.BookRepository;

import java.net.URI;
import java.util.Optional;

/**
 * REST Controller for managing book resources in the Library Management System (LMS).
 * This controller handles CRUD operations (Create, Read, Update) for books.
 * The base path for all endpoints is {@code /api/books}.
 *
 * @author LongerDude
 */
@RestController
@RequestMapping("/api/books") // Mapping to the books resource endpoint
public class BookController {
    private final BookRepository bookRepository;

    /**
     * Constructor for Dependency Injection.
     * Spring automatically injects the BookRepository implementation.
     *
     * @param bookRepository The repository used for database interaction with Book entities.
     */
    public BookController(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    /**
     * Creates a new Book resource and persists it to the database.
     *
     * @param book The {@link Book} object provided in the request body.
     * @param ucb  Utility for building the URI of the newly created resource.
     * @return A {@code 201 Created} response with the URI of the new book in the 'Location' header.
     */
    @PostMapping
    private ResponseEntity<Void> createBook(@RequestBody Book book, UriComponentsBuilder ucb) {
        // Save the new book entity. The repository generates the ID.
        Book savedBook = bookRepository.save(book);

        // Build the URI for the newly created resource, e.g., /api/books/1
        URI locationOfNewBook = ucb.path("/api/books/{id}")
                .buildAndExpand(savedBook.getId())
                .toUri();

        // Return a 201 Created response, which is standard for resource creation.
        return ResponseEntity.created(locationOfNewBook).build();
    }

    /**
     * Retrieves all existing books from the database.
     *
     * @return A {@code 200 OK} response containing an iterable collection of all {@link Book} entities.
     */
    @GetMapping
    private ResponseEntity<Iterable<Book>> getAllBooks() {
        // FindAll returns all entries, which is returned in a 200 OK response.
        return ResponseEntity.ok(bookRepository.findAll());
    }


    /**
     * Retrieves a single book by its unique identifier (ID).
     *
     * @param id The unique identifier (Long) of the book to retrieve.
     * @return A {@code 200 OK} response containing the {@link Book} if found,
     * or a {@code 404 Not Found} response otherwise.
     */
    @GetMapping("/{id}")
    private ResponseEntity<Book> getBook(@PathVariable Long id) {
        // Use Optional to handle the case where the book might not exist
        Optional<Book> book = bookRepository.findById(id);

        if (book.isPresent()) {
            // Debugging line (consider removing in production)
            System.out.println(book.get());
            return ResponseEntity.ok(book.get());
        } else {
            // Return 404 Not Found if the book does not exist
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Updates an existing book resource partially or completely.
     * Note: This implementation demonstrates partial update logic where Title and Author are retained,
     * but the amount is updated from the request body.
     *
     * @param id         The ID of the book to update.
     * @param bookUpdate The {@link Book} object containing the updated fields (e.g., amount).
     * @return A {@code 204 No Content} response upon successful update,
     * or a {@code 404 Not Found} response if the book ID is not found.
     */
    @PutMapping("/{id}")
    private ResponseEntity<Void> updateBook(@PathVariable Long id, @RequestBody Book bookUpdate) {
        // Try to find the existing book by ID
        return bookRepository.findById(id)
                .map(existingBook -> {
                    // Create a new Book instance, retaining immutable fields (ID, Title, Author)
                    // and updating the mutable fields (Amount) from the request body.
                    Book updatedBook = new Book(
                            id,
                            existingBook.getTitle(),
                            existingBook.getAuthor(),
                            bookUpdate.getAmount() // Value to be updated
                    );

                    // Save the updated entity (performs an update operation)
                    bookRepository.save(updatedBook);

                    // Return 204 No Content, which is standard for a successful update without a body
                    return ResponseEntity.noContent().<Void>build();
                })
                // If the book is not found by ID, execute the alternative path
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}