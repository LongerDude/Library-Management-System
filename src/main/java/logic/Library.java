package logic;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Manages the collection of books in the library.
 * <p>
 * It serves as the primary data access layer, using a Map to efficiently look up books by title
 * and managing the creation, borrowing, and returning of {@link Book} objects.
 */
public class Library {

    /**
     * The core storage for all books.
     * <p>
     * Key: Normalized (lowercase) book title for quick, case-insensitive lookup.
     * Value: An ArrayList of Book objects. This structure is necessary to handle the edge case
     * where multiple authors/editions share the exact same title.
     */
    private Map<String, ArrayList<Book>> books;

    /**
     * Initializes the library's internal book storage structure (a new, empty HashMap).
     */
    public Library(){
        books = new HashMap<>();
    }

    /**
     * Adds a new book or increases the quantity of an existing book (same title/author).
     * <p>
     * The title is normalized to lowercase for storage, ensuring case-insensitive searching later.
     *
     * @param title The title of the book.
     * @param author The author of the book.
     * @param quantity The number of copies to add. Must be positive.
     * @return true if the book was added/updated successfully, false otherwise (e.g., quantity <= 0).
     */
    public boolean addBook(String title, String author, int quantity){
        String normalizedTitle = title.toLowerCase();

        // Ensure an entry exists for the normalized title key, initializing an empty list if new.
        books.putIfAbsent(normalizedTitle, new ArrayList<>());
        ArrayList<Book> booksWithTheSameTitle = books.get(normalizedTitle);

        // Use Optional/Stream API to quickly check if a book with the exact author already exists under this title.
        Optional<Book> matchedBook = booksWithTheSameTitle.stream()
                .filter(book -> book.getAuthor().equalsIgnoreCase(author))
                .findFirst();

        if (matchedBook.isPresent()){
            // If the book (title + author) exists, delegate to the Book object to increase its quantity.
            return matchedBook.get().addQuantity(quantity);
        } else {
            // If the book does not exist, create a new Book object and add it to the list.
            try {
                booksWithTheSameTitle.add(new Book(title, author, quantity));
                return true;
            } catch (IllegalArgumentException e) {
                // Catches the exception thrown by the Book constructor if quantity is invalid.
                System.err.println("Error adding book: " + e.getMessage());
                return false;
            }
        }
    }

    /**
     * Searches for books by title.
     * <p>
     * Performs a case-insensitive lookup using the normalized title key.
     *
     * @param title The title to search for.
     * @return An ArrayList of Book objects matching the title. Returns an empty list if no match is found,
     * preventing NullPointerExceptions in the calling code (UI).
     */
    public ArrayList<Book> findBook(String title){

        // Normalize the search key to match the map keys.
        ArrayList<Book> matches = books.get(title.toLowerCase());

        // Return a new, empty list instead of null for safety and ease of use in UI code.
        if (matches == null) {
            return new ArrayList<Book>();
        }
        return matches;
    }


    /**
     * Delegates the borrowing operation to the specific Book object.
     *
     * @param book The specific Book instance to borrow from (retrieved via {@link #findBook(String)}).
     * @param quantity The number of copies the patron wishes to borrow.
     * @return true if successful, false otherwise.
     */
    public boolean borrowBook(Book book, int quantity){
        return book.borrow(quantity);
    }

    /**
     * Delegates the return operation to the specific Book object.
     *
     * @param book The specific Book instance to return to (retrieved via {@link #findBook(String)}).
     * @param quantity The number of copies being returned.
     * @return true if successful, false otherwise.
     */
    public boolean returnBook(Book book, int quantity){
        return book.returnBook(quantity);
    }
}