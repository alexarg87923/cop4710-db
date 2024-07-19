import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class BookService {
    private Scanner input;
    private Connection conn;
    private AuthorService authorService;
    private GenreService genreService;

    public BookService(Scanner input, Connection conn, AuthorService authorService, GenreService genreService) {
        this.input = input;
        this.conn = conn;
        this.authorService = authorService;
        this.genreService = genreService;
    }

    public void addBook() {
        System.out.println("Enter the details of the book to add:");
        System.out.print("Title: ");
        String title = input.nextLine();
    
        int authorId = handleAuthor();
        int genreId = handleGenre();
    
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
    
    public void removeBook() {
        System.out.println("Would you like to list all books or search by title? (list/search)");
        String choice = input.nextLine();
        
        if ("list".equalsIgnoreCase(choice)) {
            listBooks();
        } else if ("search".equalsIgnoreCase(choice)) {
            System.out.print("Enter part of the title to search: ");
            String partOfTitle = input.nextLine();
            searchBooks(partOfTitle);
        }
        
        System.out.print("Enter Book ID to remove or type 'cancel' to exit: ");
        String inputStr = input.nextLine();
        
        if ("cancel".equalsIgnoreCase(inputStr)) {
            System.out.println("Operation cancelled.");
            return;
        }
        
        try {
            int bookId = Integer.parseInt(inputStr);
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM books WHERE bookid = ?");
            stmt.setInt(1, bookId);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Book removed successfully!");
            } else {
                System.out.println("No book found with the specified ID.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input, operation cancelled.");
        } catch (SQLException e) {
            System.out.println("Error removing book: " + e.getMessage());
        }
    }
    
    public void editBookEntry() {
        System.out.print("Enter Book ID to edit: ");
        int bookId = Integer.parseInt(input.nextLine());
        
        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM books WHERE bookid = ?");
            stmt.setInt(1, bookId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                System.out.println("Editing book: " + rs.getString("title"));
                
                System.out.print("New Title (leave blank to keep current): ");
                String title = input.nextLine();
                if (title.isEmpty()) {
                    title = rs.getString("title");
                }

                int authorId = handleAuthor();
                if (authorId == -1) {
                    authorId = rs.getInt("authorid");
                }

                int genreId = handleGenre();
                if (genreId == -1) {
                    genreId = rs.getInt("genreid");
                }

                System.out.print("New Year of Publication (leave blank to keep current): ");
                String yearStr = input.nextLine();
                int year;
                if (yearStr.isEmpty()) {
                    year = rs.getInt("bookyear");
                } else {
                    year = Integer.parseInt(yearStr);
                }

                System.out.print("New Quantity (leave blank to keep current): ");
                String quantityStr = input.nextLine();
                int quantity;
                if (quantityStr.isEmpty()) {
                    quantity = rs.getInt("quantity");
                } else {
                    quantity = Integer.parseInt(quantityStr);
                }

                PreparedStatement updateStmt = conn.prepareStatement("UPDATE books SET title = ?, authorid = ?, genreid = ?, bookyear = ?, quantity = ? WHERE bookid = ?");
                updateStmt.setString(1, title);
                updateStmt.setInt(2, authorId);
                updateStmt.setInt(3, genreId);
                updateStmt.setInt(4, year);
                updateStmt.setInt(5, quantity);
                updateStmt.setInt(6, bookId);
                updateStmt.executeUpdate();
                
                System.out.println("Book updated successfully!");
            } else {
                System.out.println("No book found with the specified ID.");
            }
        } catch (SQLException e) {
            System.out.println("Error updating book: " + e.getMessage());
        }
    }

    private int handleAuthor() {
        System.out.println("Enter Author Name or ID (type 'list' to see all authors): ");
        String inputStr = input.nextLine();
    
        if ("list".equalsIgnoreCase(inputStr)) {
            authorService.listAuthors();
            System.out.print("Enter Author ID: ");
            return Integer.parseInt(input.nextLine());
        } else {
            try {
                int authorId = Integer.parseInt(inputStr);
                return authorId;
            } catch (NumberFormatException e) {
                // Assuming input was a name and not a number
                try {
                    PreparedStatement stmt = conn.prepareStatement("SELECT AuthorID FROM Author WHERE AuthorName = ?");
                    stmt.setString(1, inputStr);
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        return rs.getInt("AuthorID");
                    } else {
                        System.out.println("Author not found. Do you want to make this new entry? (yes/no)");
                        String decision = input.nextLine();
                        if ("yes".equalsIgnoreCase(decision)) {
                            PreparedStatement insertStmt = conn.prepareStatement("INSERT INTO Author (AuthorName) VALUES (?) RETURNING AuthorID");
                            insertStmt.setString(1, inputStr);
                            ResultSet insertRs = insertStmt.executeQuery();
                            if (insertRs.next()) {
                                return insertRs.getInt("AuthorID");
                            }
                        }
                        throw new SQLException("Author creation was cancelled or failed.");
                    }
                } catch (SQLException ex) {
                    System.out.println("Database error: " + ex.getMessage());
                    return -1; // Indicate failure
                }
            }
        }
    }

    private int handleGenre() {
        System.out.println("Enter Genre Name or ID (type 'list' to see all genres): ");
        String inputStr = input.nextLine();
    
        if ("list".equalsIgnoreCase(inputStr)) {
            genreService.listGenres();
            System.out.print("Enter Genre ID: ");
            return Integer.parseInt(input.nextLine());
        } else {
            try {
                int genreId = Integer.parseInt(inputStr);
                return genreId;
            } catch (NumberFormatException e) {
                // Assuming input was a name and not a number
                try {
                    PreparedStatement stmt = conn.prepareStatement("SELECT GenreID FROM Genre WHERE GenreName = ?");
                    stmt.setString(1, inputStr);
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        return rs.getInt("GenreID");
                    } else {
                        System.out.println("Genre not found. Do you want to make this new entry? (yes/no)");
                        String decision = input.nextLine();
                        if ("yes".equalsIgnoreCase(decision)) {
                            PreparedStatement insertStmt = conn.prepareStatement("INSERT INTO Genre (GenreName) VALUES (?) RETURNING GenreID");
                            insertStmt.setString(1, inputStr);
                            ResultSet insertRs = insertStmt.executeQuery();
                            if (insertRs.next()) {
                                return insertRs.getInt("GenreID");
                            }
                        }
                        throw new SQLException("Genre creation was cancelled or failed.");
                    }
                } catch (SQLException ex) {
                    System.out.println("Database error: " + ex.getMessage());
                    return -1; // Indicate failure
                }
            }
        }
    }

	public void extendLoanForMember() {
        // Placeholder: Implement actual checkout logic
        System.out.println("Checkout book for member (functionality not fully implemented yet).");
    }


    public void checkoutBookForMember() {
        // Placeholder: Implement actual checkout logic
        System.out.println("Checkout book for member (functionality not fully implemented yet).");
    }

    public void checkInBookForMember() {
        // Placeholder: Implement actual check-in logic
        System.out.println("Check in book for member (functionality not fully implemented yet).");
    }

    public void listBooks() {
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

    public void searchBooks(String partOfTitle) {
        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT bookid, title FROM books WHERE lower(title) LIKE ?");
            stmt.setString(1, "%" + partOfTitle.toLowerCase() + "%");
            ResultSet rs = stmt.executeQuery();
            
            boolean found = false;
            while (rs.next()) {
                if (!found) {
                    System.out.println("Matching books:");
                    found = true;
                }
                System.out.println("Book ID: " + rs.getInt("bookid") + ", Title: " + rs.getString("title"));
            }
            if (!found) {
                System.out.println("No books found with the specified title part.");
            }
        } catch (SQLException e) {
            System.out.println("Error searching for books: " + e.getMessage());
        }
    }

    public void findBook() {
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
}
