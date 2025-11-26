package UserInterface;
import logic.Book;
import logic.Library;

import java.util.ArrayList;
import java.util.Scanner;


/**
 * The console-based user interface for the Library application.
 * Handles all user input and output.
 */
public class UI {

    public static void main(String[] args){
        Scanner scanner = new Scanner(System.in);
        Library library = new Library();
        System.out.println("Welcome to the Library software!");

        // Main application loop
        while (true) {
            int choice;

            // START: Validation Loop using do-while to ensure a valid menu choice (1-4) is entered.
            do {
                System.out.println("1. Add Book");
                System.out.println("2. Borrow Book");
                System.out.println("3. Return Book");
                System.out.println("4. Exit");
                System.out.println("Please enter your choice (1-4): ");

                // Input reading/conversion with error handling
                try {
                    // Using nextLine() and parsing handles the input cleanly
                    choice = Integer.parseInt(scanner.nextLine());
                } catch (NumberFormatException e) {
                    // Catches non-integer input (like "a", "hello")
                    System.out.println("Invalid input. Please enter a number (1-4).");
                    choice = -1; // initialize to ensure the loop continues
                    continue;
                }
                // --- Range Check ---
                if (choice < 1 || choice > 4) {
                    System.out.println("Invalid choice: " + choice + ". Please select a number between 1 and 4.");
                }
            } while (choice < 1 || choice > 4);
            // END: Validation Loop


            // --- Main Logic Switch ---
            switch (choice) {
                case 1: // ADD BOOK
                    System.out.println("Author?");
                    String authorToAdd = scanner.nextLine();
                    System.out.println("Title?");
                    String titleToAdd = scanner.nextLine();
                    System.out.println("Quantity? (Enter 0 to cancel)");

                    // Use helper method to ensure positive integer input
                    int quantityToAdd = validQuantityInput(scanner);
                    if (quantityToAdd == 0) {
                        System.out.println("Action cancelled.");
                        break;
                    }

                    // Attempt to add or update the book in the library
                    try {
                        // The addBook method handles quantity validation (must be > 0),
                        // but 0 is handled above to allow user cancellation.
                        library.addBook(titleToAdd, authorToAdd, quantityToAdd);
                        System.out.println("Book added/updated successfully!");
                        System.out.println();
                        break;
                    } catch (Exception e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;

                case 2: // BORROW BOOK
                    System.out.println("Title to borrow?");
                    String titleToBorrow = scanner.nextLine();
                    // Fetch list once to avoid repeated calls to findBook()
                    ArrayList<Book> matchingBooks = library.findBook(titleToBorrow);

                    if (matchingBooks.isEmpty()) {
                        // Book not found.
                        System.out.println("Title not found.");
                        break;
                    }

                    if (matchingBooks.size() == 1) {
                        // One book (only one author/edition).
                        System.out.println("Quantity to borrow? (Enter 0 to cancel)");
                        Book bookToBorrow = matchingBooks.getFirst();

                        // Loop to allow user to try different quantities until success or cancellation.
                        while (true) {
                            int quantityToBorrow = validQuantityInput(scanner);
                            if (quantityToBorrow == 0) {
                                System.out.println("Transaction cancelled.");
                                break;
                            }
                            // Call library logic and check return status.
                            if (library.borrowBook(bookToBorrow, quantityToBorrow)) {
                                System.out.println("Book(s) borrowed successfully!");
                                break;
                            }
                            // If borrow failed, loop continues, prompting for a new quantity.
                        }
                    } else { // Multiple books of the same title (different authors/editions).
                        ArrayList<Book> booksToBorrow = matchingBooks;
                        System.out.println();

                        // Display options to the user.
                        for (int i = 0; i < booksToBorrow.size(); i++) {
                            // Displaying 1-based index for user readability
                            System.out.println((i + 1) + ". " + booksToBorrow.get(i).getTitle() + " by " +  booksToBorrow.get(i).getAuthor());
                        }
                        System.out.println("Which book exactly? (Enter the number)");

                        // Get the user's index choice and validate against the size of the list.
                        int choiceOfBook = validQuantityInput(scanner);
                        // FIX #2: Ensured choice is within the valid 1-based range.
                        while (choiceOfBook <= 0 || choiceOfBook > booksToBorrow.size()) {
                            System.out.println("Invalid input. Please enter a number between 1 and " + booksToBorrow.size() + ".");
                            choiceOfBook = validQuantityInput(scanner);
                        }

                        // FIX #1: Use choiceOfBook - 1 to convert user's 1-based index to 0-based index.
                        Book selectedBook = booksToBorrow.get(choiceOfBook - 1);

                        // Loop to allow user to try different quantities until success or cancellation.
                        while (true) {
                            System.out.println("Quantity? (Enter 0 to cancel)");
                            int quantityToBorrow = validQuantityInput(scanner);

                            if (quantityToBorrow == 0) {
                                System.out.println("Transaction cancelled.");
                                break;
                            }
                            // Call library logic and check return status.
                            // FIX #1: Use choiceOfBook - 1 to convert user's 1-based index to 0-based index.
                            if (library.borrowBook(selectedBook, quantityToBorrow)) {
                                System.out.println("Book(s) borrowed successfully!");
                                break;
                            }
                            // If borrow failed, loop continues, prompting for a new quantity.
                        }
                        System.out.println();
                        break;

                    }
                    break;

                case 3: // RETURN BOOK
                    System.out.println("Title to return?");
                    String titleToReturn = scanner.nextLine();
                    // Fetch list once to avoid repeated calls to findBook()
                    ArrayList<Book> matchingReturnBooks = library.findBook(titleToReturn);

                    if (matchingReturnBooks.isEmpty()) { // Book not found.
                        System.out.println("Title not found.");
                        break;
                    }

                    if (matchingReturnBooks.size() == 1) { // One book.

                        System.out.println("Quantity to return? (Enter 0 to cancel)"); // Initial prompt
                        Book bookToReturn = matchingReturnBooks.getFirst();

                        // Loop to allow user to try different quantities until success or cancellation.
                        while (true) {
                            // FIX #3: Removed redundant System.out.println("Quantity? (Enter 0 to cancel)");
                            int quantityToReturn = validQuantityInput(scanner);

                            if (quantityToReturn == 0) {
                                System.out.println("Transaction cancelled.");
                                break;
                            }
                            if (library.returnBook(bookToReturn, quantityToReturn)) {
                                System.out.println("Book(s) returned successfully!");
                                break;
                            }
                            // If return failed, loop continues, prompting for a new quantity.
                        }

                    } else { // Multiple books of the same title.
                        ArrayList<Book> booksToReturn = matchingReturnBooks;
                        System.out.println();

                        // Display options to the user.
                        for (int i = 0; i < booksToReturn.size(); i++) {
                            // Displaying 1-based index for user readability
                            System.out.println((i + 1) + ". " + booksToReturn.get(i).getTitle() + " by " +  booksToReturn.get(i).getAuthor());
                        }
                        System.out.println("Which book exactly? (Enter the number)");

                        // Get the user's index choice and validate against the size of the list.
                        int choiceOfBook = validQuantityInput(scanner);
                        // FIX #2: Ensured choice is within the valid 1-based range.
                        while (choiceOfBook <= 0 || choiceOfBook > booksToReturn.size()) {
                            System.out.println("Invalid input. Please enter a number between 1 and " + booksToReturn.size() + ".");
                            choiceOfBook = validQuantityInput(scanner);
                        }

                        // FIX #1: Use choiceOfBook - 1 to convert user's 1-based index to 0-based index.
                        Book selectedBook = booksToReturn.get(choiceOfBook - 1);

                        // Loop to allow user to try different quantities until success or cancellation.
                        while (true) {
                            System.out.println("Quantity? (Enter 0 to cancel)");
                            int quantityToReturn = validQuantityInput(scanner);

                            if (quantityToReturn == 0) {
                                System.out.println("Transaction cancelled.");
                                break;
                            }
                            // Call library logic and check return status.
                            // FIX #1: Use choiceOfBook - 1 to convert user's 1-based index to 0-based index.
                            if (library.returnBook(selectedBook, quantityToReturn)) {
                                System.out.println("Book(s) returned successfully!");
                                break;
                            }
                            // If return failed, loop continues, prompting for a new quantity.
                        }

                    }
                    // Removed the redundant/buggy block here.
                    break;

                case 4: // EXIT
                    System.out.println("Exiting application. Goodbye!");
                    // Use System.exit(0) to terminate the program cleanly.
                    System.exit(0);
            }
        }
    }


    /**
     * Helper method to handle input validation for quantity fields.
     * Ensures the user enters a non-negative integer (>= 0).
     * @param scanner The Scanner object for reading input.
     * @return A valid non-negative integer quantity (or 0 for cancel).
     */
    public static int validQuantityInput(Scanner scanner){
        while (true){
            try {
                int quantityToAdd = Integer.parseInt(scanner.nextLine());
                // Quantity must be non-negative. Zero is allowed for cancellation.
                if (quantityToAdd < 0 ){
                    System.out.println("Invalid input. Please enter a number greater than or equal to 0.");
                    continue;
                }
                return quantityToAdd;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter an integer.");
            }
        }
    }

}