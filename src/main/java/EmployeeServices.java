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
	
    public EmployeeServices(Scanner input) {
        this.input = input;
        try {
            this.conn = DatabaseUtil.connect();
			this.genreService = new GenreService(input, conn);
			this.authorService = new AuthorService(input, conn);
			this.bookService = new BookService(input, conn, authorService, genreService);
        } catch (SQLException e) {
            System.out.println("Error connecting to the database: " + e.getMessage());
        }
    }

	public void showEmployeeHomeScreen() {
        int option;
        do {
            System.out.println("Employee Home Screen:");
            System.out.println("1. Add Book");
            System.out.println("2. Remove Book");
            System.out.println("3. List Books");
            System.out.println("4. Edit Book Entry");
            System.out.println("5. Find Book");
            System.out.println("6. Checkout Book for Member");
            System.out.println("7. Check In Book for Member");
            System.out.println("8. Extend Loan for Member");
            System.out.println("9. Add Author");
            System.out.println("10. Remove Author");
            System.out.println("11. List Authors");
            System.out.println("12. Edit Author");
            System.out.println("13. Add Genre");
            System.out.println("14. Remove Genre");
            System.out.println("15. List Genre");
            System.out.println("16. Edit Genre");
            System.out.println("17. List All Members");
            System.out.println("18. List/Search Members");
            System.out.println("19. Log Out");
            System.out.print("Select an option: ");
            option = Integer.parseInt(input.nextLine());

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
                    System.out.println("Logging out...");
                    return; // Exit the loop on logout
                default:
                    System.out.println("Invalid option. Please try again.");
                    break;
            }
        } while (option != 19); // Exit condition corrected to match the logout option
    }

    public void listMembers() {
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
                "SELECT MemberID, Name, Email FROM Member WHERE lower(Name) LIKE ?");
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
            PreparedStatement stmt = conn.prepareStatement("SELECT MemberID, Name, Email FROM Member");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                System.out.println("Member ID: " + rs.getInt("MemberID") + ", Name: " + rs.getString("Name") + ", Email: " + rs.getString("Email"));
            }
        } catch (SQLException e) {
            System.out.println("Error listing members: " + e.getMessage());
        }
    }
}
