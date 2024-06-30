import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class EmployeeServices {

    private Scanner input;
    private Connection conn;

    public EmployeeServices(Scanner input) {
        this.input = input;
        try {
            this.conn = DatabaseUtil.connect();
        } catch (SQLException e) {
            System.out.println("Error connecting to the database: " + e.getMessage());
        }
    }

    public void showEmployeeHomeScreen() {
        int option;
        do {
            System.out.println("Employee Home Screen:");
            System.out.println("1. Add Book");
            System.out.println("2. Checkout Book for Member");
            System.out.println("3. Check In Book for Member");
            System.out.println("4. Remove Book");
            System.out.println("5. List Books");
            System.out.println("6. Edit Book Entry");
            System.out.println("7. Find Book");
            System.out.println("8. Extend Loan for Member");
            System.out.println("9. Log Out");
            System.out.print("Select an option: ");
            option = Integer.parseInt(input.nextLine());

            switch (option) {
                case 1:
                    addBook();
                    break;
                case 2:
                    checkoutBookForMember();
                    break;
                case 3:
                    checkInBookForMember();
                    break;
                case 4:
                    removeBook();
                    break;
                case 5:
                    listBooks();
                    break;
                case 6:
                    editBookEntry();
                    break;
                case 7:
                    findBook();
                    break;
                case 8:
                    extendLoanForMember();
                    break;
                case 9:
                    System.out.println("Logging out...");
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
                    break;
            }
        } while (option != 9);
    }

    private void addBook() {
        System.out.println("Enter the details of the book to add:");
        System.out.print("Title: ");
        String title = input.nextLine();
        System.out.print("Author ID: ");
        int authorId = Integer.parseInt(input.nextLine());
        System.out.print("Genre ID: ");
        int genreId = Integer.parseInt(input.nextLine());
        System.out.print("Year of Publication: ");
        int year = Integer.parseInt(input.nextLine());
        System.out.print("Quantity: ");
        int quantity = Integer.parseInt(input.nextLine());

        try {
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO books (title, authorid, genreid, bookyear, quantity) VALUES (?, ?, ?, ?, ?)");
            stmt.setString(1, title);
            stmt.setInt(2, authorId);
            stmt.setInt(3, genreId);
            stmt.setInt(4, year);
            stmt.setInt(5, quantity);
            stmt.executeUpdate();
            System.out.println("Book added successfully!");
        } catch (SQLException e) {
            System.out.println("Failed to add book: " + e.getMessage());
        }
    }

    private void checkoutBookForMember() {
        // Placeholder: Implement actual checkout logic
        System.out.println("Checkout book for member (functionality not fully implemented yet).");
    }

    private void checkInBookForMember() {
        // Placeholder: Implement actual check-in logic
        System.out.println("Check in book for member (functionality not fully implemented yet).");
    }

    private void removeBook() {
        System.out.print("Enter Book ID to remove: ");
        int bookId = Integer.parseInt(input.nextLine());
        try {
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM books WHERE bookid = ?");
            stmt.setInt(1, bookId);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Book removed successfully!");
            } else {
                System.out.println("No book found with the specified ID.");
            }
        } catch (SQLException e) {
            System.out.println("Error removing book: " + e.getMessage());
        }
    }

    private void listBooks() {
        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM books");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                System.out.println("Book ID: " + rs.getInt("bookid") + ", Title: " + rs.getString("title"));
            }
        } catch (SQLException e) {
            System.out.println("Error listing books: " + e.getMessage());
        }
    }

    private void editBookEntry() {
        // Placeholder: Implement actual edit logic
        System.out.println("Edit book entry (functionality not fully implemented yet).");
    }

    private void findBook() {
        System.out.print("Enter title or part of title to search: ");
        String title = input.nextLine();
        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM books WHERE lower(title) LIKE ?");
            stmt.setString(1, "%" + title.toLowerCase() + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                System.out.println("Book ID: " + rs.getInt("bookid") + ", Title: " + rs.getString("title"));
            }
        } catch (SQLException e) {
            System.out.println("Error finding book: " + e.getMessage());
        }
    }

    private void extendLoanForMember() {
        // Placeholder: Implement actual loan extension logic
        System.out.println("Extend loan for member (functionality not fully implemented yet).");
    }
}
