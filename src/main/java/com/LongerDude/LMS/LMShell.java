package com.LongerDude.LMS;

import com.LongerDude.LMS.model.Book;
import com.LongerDude.LMS.repository.BookRepository;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * A Spring Shell component providing a simple command-line interface (CLI)
 * for the Library Management System (LMS). This component interacts directly
 * with the BookRepository to manage book inventory.
 */
@ShellComponent
public class LMShell {

    private static final String NOT_FOUND_MESSAGE = "Error: Book ID not found.";
    private static BookRepository bookRepository;

    /**
     * Constructor for dependency injection. Spring automatically provides
     * the generated implementation of BookRepository.
     *
     * @param bookRepository The JPA repository for Book entities.
     */
    public LMShell(BookRepository bookRepository) {
        // Since this is a static method-based shell component, we store the repository statically.
        LMShell.bookRepository = bookRepository;
    }

    /**
     * The main entry point for the CLI application.
     * This method displays the main menu and handles user input until exit.
     */
    @ShellMethod(key = "start", value = "Starts the main application loop and menu.")
    public static void start() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to the Library software!");

        // Main application loop
        while (true) {
            int choice;

            // START: Menu Validation Loop
            do {
                System.out.println("\n--- Main Menu ---");
                System.out.println("1. Add Book");
                System.out.println("2. Borrow Book");
                System.out.println("3. Return Book");
                System.out.println("4. Exit");
                System.out.print("Please enter your choice (1-4): ");

                try {
                    choice = Integer.parseInt(scanner.nextLine());
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a number (1-4).");
                    choice = -1; // Ensure loop continues
                    continue;
                }
                // --- Range Check ---
                if (choice < 1 || choice > 4) {
                    System.out.println("Invalid choice: " + choice + ". Please select a number between 1 and 4.");
                }
            } while (choice < 1 || choice > 4);
            // END: Menu Validation Loop


            // --- Main Logic Switch ---
            try {
                switch (choice) {
                    case 1: // ADD BOOK
                        handleAddBook(scanner);
                        break;

                    case 2: // BORROW BOOK
                        handleBorrowBook(scanner); // Corrected to new dedicated method
                        break;

                    case 3: // RETURN BOOK
                        handleReturnBook(scanner); // Corrected to new dedicated method
                        break;

                    case 4: // EXIT
                        System.out.println("Exiting application. Goodbye!");
                        System.exit(0);
                }
            } catch (Exception e) {
                System.err.println("An unexpected error occurred during operation: " + e.getMessage());
            }
        }
    }


    // -------------------------------------------------------------------------
    // UTILITY METHODS
    // -------------------------------------------------------------------------

    /**
     * Prints the current list of all books in the inventory with their IDs and stock.
     */
    private static void printBooks() {
        System.out.println("\n--- Current Inventory ---");
        List<Book> books = bookRepository.findAll();
        if (books.isEmpty()) {
            System.out.println("The library is currently empty.");
            return;
        }
        for (Book book : books) {
            // Uses the Book entity's toString() method for formatted output
            System.out.println("ID: " + book.getId() + " | " + book);
        }
    }

    /**
     * Safely reads and validates integer input for quantity, ensuring it is non-negative.
     *
     * @param scanner The Scanner object for reading console input.
     * @return A valid integer quantity.
     */
    public static int validQuantityInput(Scanner scanner) {
        while (true) {
            try {
                // Use nextLine() for consistent input handling
                int quantity = Integer.parseInt(scanner.nextLine());

                // Quantity must be non-negative. Zero is allowed for cancellation.
                if (quantity < 0) {
                    System.out.println("Invalid input. Please enter a number greater than or equal to 0.");
                    continue;
                }
                return quantity;
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter an integer: ");
            }
        }
    }

    /**
     * Safely reads and validates Long input for IDs, ensuring it is non-negative.
     *
     * @param scanner The Scanner object for reading console input.
     * @return A valid long ID.
     */
    public static long validQuantityInputLong(Scanner scanner) {
        while (true) {
            try {
                // Use Long.parseLong() for IDs
                long id = Long.parseLong(scanner.nextLine());

                // ID must be non-negative.
                if (id <= 0) {
                    System.out.println("Invalid input. Please enter a positive ID.");
                    continue;
                }
                return id;
            } catch (NumberFormatException e) {
                // Catches input that cannot be parsed as a long integer (e.g., "abc")
                System.out.print("Invalid input. Please enter a whole number for the ID: ");
            }
        }
    }

    // -------------------------------------------------------------------------
    // PRIMARY HANDLER METHODS
    // -------------------------------------------------------------------------

    /**
     * Handles the entire "Add Book" workflow, including input and saving a new book.
     *
     * @param scanner The Scanner object for reading console input.
     */
    private static void handleAddBook(Scanner scanner) {
        System.out.print("Enter Author: ");
        String authorToAdd = scanner.nextLine();
        System.out.print("Enter Title: ");
        String titleToAdd = scanner.nextLine();
        System.out.print("Enter Quantity (0 to cancel): ");

        int quantityToAdd = validQuantityInput(scanner);
        if (quantityToAdd == 0) {
            System.out.println("Action cancelled.");
            return;
        }

        try {
            // Note: If you want to check if a book with the same title/author exists and only update amount,
            // you'd need a custom query method in BookRepository (e.g., findByTitleAndAuthor).
            bookRepository.save(new Book(titleToAdd, authorToAdd, quantityToAdd));
            System.out.println("Book added successfully: " + titleToAdd);
        } catch (Exception e) {
            System.out.println("Error adding book: " + e.getMessage());
        }
    }

    /**
     * Handles the "Borrow Book" workflow, decreasing the available stock.
     *
     * @param scanner The Scanner object for reading console input.
     */
    private static void handleBorrowBook(Scanner scanner) {
        printBooks();
        System.out.print("Enter the ID of the book to borrow: ");
        long id = validQuantityInputLong(scanner);

        Optional<Book> optionalBook = bookRepository.findById(id);

        if (optionalBook.isEmpty()) {
            System.out.println(NOT_FOUND_MESSAGE);
            return;
        }

        Book existingBook = optionalBook.get();
        System.out.print("How many copies of '" + existingBook.getTitle() + "' would you like to borrow? ");
        int amountToBorrow = validQuantityInput(scanner);

        if (amountToBorrow <= 0) {
            System.out.println("Borrow action cancelled.");
            return;
        }

        if (existingBook.getAmount() < amountToBorrow) {
            System.out.printf("Error: Only %d copies of '%s' are available (requested: %d).\n",
                    existingBook.getAmount(), existingBook.getTitle(), amountToBorrow);
            return;
        }

        // Calculate the new stock amount
        int newAmount = existingBook.getAmount() - amountToBorrow;

        // Create a new Book instance for the update (ID is crucial here)
        Book updatedBook = new Book(
                id,
                existingBook.getTitle(),
                existingBook.getAuthor(),
                newAmount
        );

        bookRepository.save(updatedBook);
        System.out.printf("Successfully borrowed %d copies of '%s'. %d remaining.\n",
                amountToBorrow, existingBook.getTitle(), newAmount);
    }

    /**
     * Handles the "Return Book" workflow, increasing the available stock.
     *
     * @param scanner The Scanner object for reading console input.
     */
    private static void handleReturnBook(Scanner scanner) {
        printBooks();
        System.out.print("Enter the ID of the book being returned: ");
        long id = validQuantityInputLong(scanner);

        Optional<Book> optionalBook = bookRepository.findById(id);

        if (optionalBook.isEmpty()) {
            System.out.println(NOT_FOUND_MESSAGE);
            return;
        }

        Book existingBook = optionalBook.get();
        System.out.print("How many copies of '" + existingBook.getTitle() + "' are you returning? ");
        int amountToReturn = validQuantityInput(scanner);

        if (amountToReturn <= 0) {
            System.out.println("Return action cancelled.");
            return;
        }

        // Calculate the new stock amount (always increases)
        int newAmount = existingBook.getAmount() + amountToReturn;

        // Create a new Book instance for the update
        Book updatedBook = new Book(
                id,
                existingBook.getTitle(),
                existingBook.getAuthor(),
                newAmount
        );

        bookRepository.save(updatedBook);
        System.out.printf("Successfully returned %d copies of '%s'. Total stock: %d.\n",
                amountToReturn, existingBook.getTitle(), newAmount);
    }
}