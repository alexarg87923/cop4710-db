import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class MemberServices {

    private Scanner input;
    private Connection conn;

    public MemberServices(Scanner input) {
        this.input = input;
        try {
            this.conn = DatabaseUtil.connect();
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
                    searchBooks();
                    break;
                case 2:
                    listBooks();
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

    private void searchBooks() {
        System.out.print("Enter book title or part of title to search: ");
        String title = input.nextLine();
        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM books WHERE lower(title) LIKE ?");
            stmt.setString(1, "%" + title.toLowerCase() + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                System.out.println("Book ID: " + rs.getInt("bookid") + ", Title: " + rs.getString("title"));
            }
        } catch (SQLException e) {
            System.out.println("Error searching books: " + e.getMessage());
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

    private void checkoutBook() {
        System.out.print("Enter Book ID to checkout: ");
        int bookId = Integer.parseInt(input.nextLine());
        // Implement checkout logic
        System.out.println("Checkout process for Book ID: " + bookId + " (Functionality not fully implemented yet)");
    }

    private void checkInBook() {
        System.out.print("Enter Book ID to check in: ");
        int bookId = Integer.parseInt(input.nextLine());
        // Implement check-in logic
        System.out.println("Check-in process for Book ID: " + bookId + " (Functionality not fully implemented yet)");
    }
}
