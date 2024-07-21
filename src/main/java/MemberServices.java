import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Scanner;

public class MemberServices {
    public enum BookListStatus {
        SUCCESS, // No issues, may or may not have books
        NO_BOOKS, // No books checked out
        ERROR // Error occurred
    }

    private Scanner input;
    private Connection conn;
    private BookService bookService;
    private int loggedInMemberId;

    public MemberServices(Scanner input, int loggedInMemberId, Connection conn) {
        this.input = input;
        this.loggedInMemberId = loggedInMemberId;
        this.conn = conn;
        AuthorService authorService = new AuthorService(input, conn);
        GenreService genreService = new GenreService(input, conn);
        this.bookService = new BookService(input, conn, authorService, genreService);
    }

    public void showMemberHomeScreen() {
        int option;
        do {
            System.out.println("\n================ Member Home Screen ================");
            System.out.println("1. Search Books");
            System.out.println("2. List Books");
            System.out.println("3. Checkout Book");
            System.out.println("4. Check In Book");
            System.out.println("5. Log Out");
            System.out.print("Select an option: ");
            try {
                option = Integer.parseInt(input.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
                option = -1;
                continue;
            }

            switch (option) {
                case 1:
                    bookService.findBook();
                    break;
                case 2:
                    bookService.listBooks();
                    break;
                case 3:
                    checkoutBook();
                    break;
                case 4:
                    checkInBook();
                    break;
                case 5:
                    System.out.println("Logging out...");
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
                    break;
            }
        } while (option != 5);
    }

    private BookListStatus listMemberBooks() {
        try {
            int rowCount = 0;
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT * FROM BookLoans bl JOIN Books b ON bl.BookID = b.BookID WHERE bl.MemberID = ? AND bl.BookReturn = 'N'");
            stmt.setInt(1, loggedInMemberId);
            ResultSet rs = stmt.executeQuery();
            System.out.println("\n========== Books Currently Checked Out ==========");
            
            while (rs.next()) {
                rowCount++;
                System.out.println("Loan ID: " + rs.getInt("LoanID") + ", Book ID: " + rs.getInt("BookID") + ", Title: " + rs.getString("Title") + ", Due Date: " + rs.getDate("DueDate"));
            }
            
            if (rowCount == 0) {
                System.out.println("You currently don't have any books checked out.");
                return BookListStatus.NO_BOOKS;
            }
            return BookListStatus.SUCCESS;
        } catch (SQLException e) {
            System.out.println("Error listing member's books: " + e.getMessage());
            return BookListStatus.ERROR;
        }
    }

    private void checkInBook() {
        System.out.println("\n======== Check-in Book ========");
        BookListStatus result = listMemberBooks();
        if (result == BookListStatus.ERROR) {
            System.out.println("Unable to proceed due to an error.");
            return;
        } else if (result == BookListStatus.NO_BOOKS) {
            System.out.println("No books to return.");
            return;
        }

        System.out.print("Enter Book ID to return (or 'cancel' to exit): ");
        String inputLine = input.nextLine();

        if (inputLine.equalsIgnoreCase("cancel")) {
            System.out.println("Operation cancelled.");
            return;
        }

        try {
            int bookId = Integer.parseInt(inputLine);
            LocalDate returnDate = LocalDate.now();

            PreparedStatement stmt = conn.prepareStatement(
                "UPDATE BookLoans SET ReturnDate = ?, BookReturn = 'Y' WHERE BookID = ? AND MemberID = ? AND BookReturn = 'N'");
            stmt.setDate(1, java.sql.Date.valueOf(returnDate));
            stmt.setInt(2, bookId);
            stmt.setInt(3, loggedInMemberId);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                conn.commit();  // Committing the transaction
                System.out.println("Book returned successfully!");
            } else {
                System.out.println("No matching loan record found or the book is already returned.");
            }
        } catch (NumberFormatException | SQLException e) {
            System.out.println("Failed to return book: " + e.getMessage());
            try {
                conn.rollback();  // Rolling back in case of error
            } catch (SQLException ex) {
                System.out.println("Error during rollback: " + ex.getMessage());
            }
        }
    }

    private void checkoutBook() {
        System.out.println("\n======== Check-out Book ========");
        System.out.print("Would you like to list all books? (yes/no/cancel): ");
        String choice = input.nextLine();

        if (choice.equalsIgnoreCase("cancel")) {
            System.out.println("Operation cancelled.");
            return;
        }

        if (choice.equalsIgnoreCase("yes")) {
            listAllBooks();
        }

        System.out.print("Enter Book ID to rent (or 'cancel' to exit): ");
        String bookIdInput = input.nextLine();

        if (bookIdInput.equalsIgnoreCase("cancel")) {
            System.out.println("Operation cancelled.");
            return;
        }

        try {
            int bookId = Integer.parseInt(bookIdInput);
            LocalDate loanDate = LocalDate.now();
            LocalDate dueDate = loanDate.plusDays(14); // Assuming a 14-day loan period

            PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO BookLoans (BookID, MemberID, LoanDate, DueDate, BookReturn, LoanPrice) VALUES (?, ?, ?, ?, 'N', 15)");
            stmt.setInt(1, bookId);
            stmt.setInt(2, loggedInMemberId);
            stmt.setDate(3, java.sql.Date.valueOf(loanDate));
            stmt.setDate(4, java.sql.Date.valueOf(dueDate));

            stmt.executeUpdate();
            conn.commit();  // Committing the transaction
            System.out.println("Book rented successfully!");
        } catch (NumberFormatException | SQLException e) {
            System.out.println("Failed to rent book: " + e.getMessage());
            try {
                conn.rollback();  // Rolling back in case of error
            } catch (SQLException ex) {
                System.out.println("Error during rollback: " + ex.getMessage());
            }
        }
    }

    private void listAllBooks() {
        System.out.println("\n======== List of All Books ========");
        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT BookID, Title FROM Books");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                System.out.println("Book ID: " + rs.getInt("BookID") + ", Title: " + rs.getString("Title"));
            }
        } catch (SQLException e) {
            System.out.println("Failed to list books: " + e.getMessage());
        }
    }
}
