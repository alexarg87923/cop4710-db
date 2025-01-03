import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class EmployeeServices {
    private Scanner input;
    private Connection conn;
    private GenreService genreService;
    private AuthorService authorService;
    private BookService bookService;

    public EmployeeServices(Scanner input, Connection conn) {
        this.input = input;
        this.conn = conn;
        this.genreService = new GenreService(input, conn);
        this.authorService = new AuthorService(input, conn);
        this.bookService = new BookService(input, conn, authorService, genreService);
    }

    public void showEmployeeHomeScreen() {
        int option = 0;
        do {
            System.out.println("\n==================== Employee Home Screen ====================");
            System.out.println("1. Add Book\n2. Remove Book\n3. List Books\n4. Edit Book Entry");
            System.out.println("5. Find Book\n6. Checkout Book for Member\n7. Check In Book for Member");
            System.out.println("8. Extend Loan for Member\n9. Add Author\n10. Remove Author");
            System.out.println("11. List Authors\n12. Edit Author\n13. Add Genre");
            System.out.println("14. Remove Genre\n15. List Genre\n16. Edit Genre");
            System.out.println("17. List All Members\n18. List/Search Members\n19. View Loan Records\n20. Log Out");
            System.out.print("Select an option: ");
            try {
                option = Integer.parseInt(input.nextLine());
                System.out.println("\n============================================================");

                switch (option) {
                    case 1:
                        bookService.addBook();
                        break;
                    case 2:
                        bookService.removeBook();
                        break;
                    case 3:
                        bookService.listBooks();
                        break;
                    case 4:
                        bookService.editBookEntry();
                        break;
                    case 5:
                        bookService.findBook();
                        break;
                    case 6:
                        bookService.checkoutBookForMember();
                        break;
                    case 7:
                        bookService.checkInBookForMember();
                        break;
                    case 8:
                        bookService.extendLoanForMember();
                        break;
                    case 9:
                        authorService.addAuthor();
                        break;
                    case 10:
                        authorService.removeAuthor();
                        break;
                    case 11:
                        authorService.listAuthors();
                        break;
                    case 12:
                        authorService.editAuthor();
                        break;
                    case 13:
                        genreService.addGenre();
                        break;
                    case 14:
                        genreService.removeGenre();
                        break;
                    case 15:
                        genreService.listGenres();
                        break;
                    case 16:
                        genreService.editGenre();
                        break;
                    case 17:
                        listAllMembers();
                        break;
                    case 18:
                        listMembers();
                        break;
                    case 19:
                        listLoanRecords();
                        break;
                    case 20:
                        System.out.println("Logging out...");
                        return; // Exit the loop on logout
                    default:
                        System.out.println("Invalid option. Please try again.");
                        break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        } while (option != 20); // Ensure loop exits correctly
    }

    // Methods for listing and searching do not modify database state, hence do not require transaction management (commit/rollback).
    public void listLoanRecords() {
        System.out.println("Would you like to search loan records by member name or list all loan records? (search/list, type 'cancel' to exit)");
        String choice = input.nextLine();

        if ("cancel".equalsIgnoreCase(choice)) {
            System.out.println("Operation cancelled.");
            return;
        }

        if ("search".equalsIgnoreCase(choice)) {
            System.out.print("Enter member name or part of the name to search (type 'cancel' to exit): ");
            String name = input.nextLine();
            if ("cancel".equalsIgnoreCase(name)) {
                System.out.println("Operation cancelled.");
                return;
            }
            searchLoanRecordsByMemberName(name);
        } else {
            listAllLoanRecords();
        }
    }

    private void listAllLoanRecords() {
        String sql = "SELECT * FROM loan_records";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            printLoanRecords(rs);
        } catch (SQLException e) {
            System.out.println("Error fetching loan records: " + e.getMessage());
        }
    }

    private void searchLoanRecordsByMemberName(String name) {
        String sql = "SELECT * FROM loan_records WHERE MemberName ILIKE ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + name + "%");
            ResultSet rs = stmt.executeQuery();
            printLoanRecords(rs);
        } catch (SQLException e) {
            System.out.println("Error searching loan records: " + e.getMessage());
        }
    }

    private void printLoanRecords(ResultSet rs) throws SQLException {
        while (rs.next()) {
            System.out.println("Loan ID: " + rs.getInt("LoanID") +
                ", Book: " + rs.getString("BookTitle") +
                ", Member: " + rs.getString("MemberName") +
                ", Loan Date: " + rs.getDate("LoanDate") +
                ", Return Date: " + rs.getDate("ReturnDate") +
                ", Due Date: " + rs.getDate("DueDate") +
                ", Book Return: " + rs.getString("BookReturn") +
                ", Loan Price: " + rs.getDouble("LoanPrice"));
        }
    }

    private void listMembers() {
        System.out.println("Would you like to search members by name or list all members? (search/list)");
        String choice = input.nextLine();
    
        if ("search".equalsIgnoreCase(choice)) {
            System.out.print("Enter member name or part of the name to search: ");
            String name = input.nextLine();
            searchMembers(name);
        } else {
            listAllMembers();
        }
    }

    private void searchMembers(String name) {
        try {
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT m.MemberID, u.Name, u.Email " +
                "FROM Member m " +
                "JOIN \"User\" u ON m.MemberID = u.UserID " +
                "WHERE lower(u.Name) LIKE ?");
            stmt.setString(1, "%" + name.toLowerCase() + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                System.out.println("Member ID: " + rs.getInt("MemberID") + ", Name: " + rs.getString("Name") + ", Email: " + rs.getString("Email"));
            }
        } catch (SQLException e) {
            System.out.println("Error searching for members: " + e.getMessage());
        }
    }

    private void listAllMembers() {
        try {
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT m.MemberID, u.Name, u.Email " +
                "FROM Member m " +
                "JOIN \"User\" u ON m.MemberID = u.UserID");
            ResultSet rs = stmt.executeQuery();
            System.out.println("\n=========== List of All Members ===========");
            while (rs.next()) {
                System.out.println("Member ID: " + rs.getInt("MemberID") + ", Name: " + rs.getString("Name") + ", Email: " + rs.getString("Email"));
            }
        } catch (SQLException e) {
            System.out.println("Error listing members: " + e.getMessage());
        }
    }    
}
