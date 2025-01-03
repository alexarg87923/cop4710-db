import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class AuthorService {
    private Scanner input;
    private Connection conn;

    public AuthorService(Scanner input, Connection conn) {
        this.input = input;
        this.conn = conn;
    }

    public void addAuthor() {
        System.out.println("=====================================");
        System.out.print("Enter Author Name (or 'cancel' to exit): ");
        String authorName = input.nextLine();

        if (authorName.equalsIgnoreCase("cancel")) {
            return;
        }

        try {
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO Author (AuthorName) VALUES (?)");
            stmt.setString(1, authorName);
            stmt.executeUpdate();
            conn.commit();  // Committing the transaction
            System.out.println("Author added successfully!");
        } catch (SQLException e) {
            System.out.println("Failed to add author: " + e.getMessage());
            try {
                conn.rollback();  // Rolling back in case of error
            } catch (SQLException ex) {
                System.out.println("Error during rollback: " + ex.getMessage());
            }
        }
        System.out.println("=====================================");
    }

    public void removeAuthor() {
        System.out.println("=====================================");
        System.out.print("Enter Author ID to remove or type 'list' to view all authors (or 'cancel' to exit): ");
        String authorInput = input.nextLine();

        if (authorInput.equalsIgnoreCase("list")) {
            listAuthors();
            System.out.print("Enter Author ID to remove: ");
            authorInput = input.nextLine();
        } else if (authorInput.equalsIgnoreCase("cancel")) {
            return;
        }

        try {
            int authorId = Integer.parseInt(authorInput);
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM Author WHERE AuthorID = ?");
            stmt.setInt(1, authorId);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                conn.commit();  // Committing the transaction
                System.out.println("Author removed successfully!");
            } else {
                System.out.println("No author found with the specified ID.");
            }
        } catch (SQLException | NumberFormatException e) {
            System.out.println("Error removing author: " + e.getMessage());
            try {
                conn.rollback();  // Rolling back in case of error
            } catch (SQLException ex) {
                System.out.println("Error during rollback: " + ex.getMessage());
            }
        }
        System.out.println("=====================================");
    }

    public void editAuthor() {
        System.out.println("=====================================");
        System.out.print("Enter Author ID to edit or type 'list' to view all authors (or 'cancel' to exit): ");
        String authorInput = input.nextLine();

        if (authorInput.equalsIgnoreCase("list")) {
            listAuthors();
            System.out.print("Enter Author ID to edit: ");
            authorInput = input.nextLine();
        } else if (authorInput.equalsIgnoreCase("cancel")) {
            return;
        }

        try {
            int authorId = Integer.parseInt(authorInput);
            System.out.print("Enter new Author Name (or 'cancel' to exit): ");
            String newAuthorName = input.nextLine();

            if (newAuthorName.equalsIgnoreCase("cancel")) {
                return;
            }

            PreparedStatement stmt = conn.prepareStatement("UPDATE Author SET AuthorName = ? WHERE AuthorID = ?");
            stmt.setString(1, newAuthorName);
            stmt.setInt(2, authorId);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                conn.commit();  // Committing the transaction
                System.out.println("Author updated successfully!");
            } else {
                System.out.println("No author found with the specified ID.");
            }
        } catch (SQLException | NumberFormatException e) {
            System.out.println("Error updating author: " + e.getMessage());
            try {
                conn.rollback();  // Rolling back in case of error
            } catch (SQLException ex) {
                System.out.println("Error during rollback: " + ex.getMessage());
            }
        }
        System.out.println("=====================================");
    }

    public void listAuthors() {
        System.out.println("\n======== List of Authors ========");
        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT AuthorID, AuthorName FROM Author");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                System.out.println("Author ID: " + rs.getInt("AuthorID") + ", Author Name: " + rs.getString("AuthorName"));
            }
        } catch (SQLException e) {
            System.out.println("Failed to list authors: " + e.getMessage());
        }
    }
}
