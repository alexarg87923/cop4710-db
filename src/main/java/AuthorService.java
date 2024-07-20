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
        System.out.print("Enter Author Name: ");
        String authorName = input.nextLine();
        try {
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO Author (AuthorName) VALUES (?)");
            stmt.setString(1, authorName);
            stmt.executeUpdate();
            System.out.println("Author added successfully!");
        } catch (SQLException e) {
            System.out.println("Failed to add author: " + e.getMessage());
        }
        System.out.println("=====================================");
    }

    public void removeAuthor() {
        System.out.println("=====================================");
        System.out.print("Enter Author ID to remove: ");
        try {
            int authorId = Integer.parseInt(input.nextLine());
            try {
                PreparedStatement stmt = conn.prepareStatement("DELETE FROM Author WHERE AuthorID = ?");
                stmt.setInt(1, authorId);
                int affectedRows = stmt.executeUpdate();
                if (affectedRows > 0) {
                    System.out.println("Author removed successfully!");
                } else {
                    System.out.println("No author found with the specified ID.");
                }
            } catch (SQLException e) {
                System.out.println("Error removing author: " + e.getMessage());
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a valid author ID.");
        }
        System.out.println("=====================================");
    }

    public void listAuthors() {
        System.out.println("=====================================");
        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Author");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                System.out.println("Author ID: " + rs.getInt("AuthorID") + ", Author Name: " + rs.getString("AuthorName"));
            }
        } catch (SQLException e) {
            System.out.println("Error listing authors: " + e.getMessage());
        }
        System.out.println("=====================================");
    }

    public void editAuthor() {
        System.out.println("=====================================");
        System.out.print("Enter Author ID to edit: ");
        try {
            int authorId = Integer.parseInt(input.nextLine());
            System.out.print("Enter new Author Name: ");
            String newAuthorName = input.nextLine();

            try {
                PreparedStatement stmt = conn.prepareStatement("UPDATE Author SET AuthorName = ? WHERE AuthorID = ?");
                stmt.setString(1, newAuthorName);
                stmt.setInt(2, authorId);
                int affectedRows = stmt.executeUpdate();
                if (affectedRows > 0) {
                    System.out.println("Author updated successfully!");
                } else {
                    System.out.println("No author found with the specified ID.");
                }
            } catch (SQLException e) {
                System.out.println("Error updating author: " + e.getMessage());
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a valid author ID.");
        }
        System.out.println("=====================================");
    }
}
