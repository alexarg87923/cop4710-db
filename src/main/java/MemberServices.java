import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Scanner;

public class MemberServices {

    private Scanner input;
    private Connection conn;
    private BookService bookService;

    public MemberServices(Scanner input) {
        this.input = input;
        try {
            this.conn = DatabaseUtil.connect();
            AuthorService authorService = new AuthorService(input, conn);
            GenreService genreService = new GenreService(input, conn);
            this.bookService = new BookService(input, conn, authorService, genreService);
        } catch (SQLException e) {
            System.out.println("Error connecting to the database: " + e.getMessage());
        }
    }

    public void showMemberHomeScreen() {
        int option;
        do {
            System.out.println("Member Home Screen:");
            System.out.println("1. Search Books");
            System.out.println("2. List Books");
            System.out.println("3. Checkout Book");
            System.out.println("4. Check In Book");
            System.out.println("5. Log Out");
            System.out.print("Select an option: ");
            option = Integer.parseInt(input.nextLine());

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
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
                    break;
            }
        } while (option != 5);
    }


    private void checkoutBook() {
        System.out.print("Enter Book ID to checkout: ");
        int bookId = Integer.parseInt(input.nextLine());
        System.out.print("Enter Member ID: ");
        int memberId = Integer.parseInt(input.nextLine());

        LocalDate loanDate = LocalDate.now();
        LocalDate dueDate = loanDate.plusDays(14); // Assuming a 14-day loan period

        try {
            PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO BookLoans (BookID, MemberID, LoanDate, DueDate, BookReturn, LoanPrice) VALUES (?, ?, ?, ?, 'N', 15)");
            stmt.setInt(1, bookId);
            stmt.setInt(2, memberId);
            stmt.setDate(3, java.sql.Date.valueOf(loanDate));
            stmt.setDate(4, java.sql.Date.valueOf(dueDate));
            stmt.executeUpdate();
            System.out.println("Book checked out successfully!");
        } catch (SQLException e) {
            System.out.println("Failed to checkout book: " + e.getMessage());
        }
    }

    private void checkInBook() {
        System.out.print("Enter Book ID to check in: ");
        int bookId = Integer.parseInt(input.nextLine());
        System.out.print("Enter Member ID: ");
        int memberId = Integer.parseInt(input.nextLine());

        LocalDate returnDate = LocalDate.now();

        try {
            PreparedStatement stmt = conn.prepareStatement(
                "UPDATE BookLoans SET ReturnDate = ?, BookReturn = 'Y' WHERE BookID = ? AND MemberID = ? AND BookReturn = 'N'");
            stmt.setDate(1, java.sql.Date.valueOf(returnDate));
            stmt.setInt(2, bookId);
            stmt.setInt(3, memberId);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Book checked in successfully!");
            } else {
                System.out.println("No matching loan record found or the book is already returned.");
            }
        } catch (SQLException e) {
            System.out.println("Failed to check in book: " + e.getMessage());
        }
    }
}
