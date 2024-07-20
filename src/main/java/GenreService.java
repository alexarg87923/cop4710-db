import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class GenreService {
    private Scanner input;
    private Connection conn;

    public GenreService(Scanner input, Connection conn) {
        this.input = input;
        this.conn = conn;
    }

    public void addGenre() {
        System.out.println("\n======== Add New Genre ========");
        System.out.print("Enter Genre Name: ");
        String genreName = input.nextLine();
        try {
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO Genre (GenreName) VALUES (?)");
            stmt.setString(1, genreName);
            stmt.executeUpdate();
            System.out.println("Genre added successfully!");
        } catch (SQLException e) {
            System.out.println("Failed to add genre: " + e.getMessage());
        }
    }

    public void removeGenre() {
        System.out.println("\n======== Remove Genre ========");
        System.out.print("Enter Genre ID to remove: ");
        try {
            int genreId = Integer.parseInt(input.nextLine());
            try {
                PreparedStatement stmt = conn.prepareStatement("DELETE FROM Genre WHERE GenreID = ?");
                stmt.setInt(1, genreId);
                int affectedRows = stmt.executeUpdate();
                if (affectedRows > 0) {
                    System.out.println("Genre removed successfully!");
                } else {
                    System.out.println("No genre found with the specified ID.");
                }
            } catch (SQLException e) {
                System.out.println("Error removing genre: " + e.getMessage());
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a valid genre ID.");
        }
    }

    public void listGenres() {
        System.out.println("\n======== List of Genres ========");
        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Genre");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                System.out.println("Genre ID: " + rs.getInt("GenreID") + ", Genre Name: " + rs.getString("GenreName"));
            }
        } catch (SQLException e) {
            System.out.println("Error listing genres: " + e.getMessage());
        }
    }

    public void editGenre() {
        System.out.println("\n======== Edit Genre ========");
        System.out.print("Enter Genre ID to edit: ");
        try {
            int genreId = Integer.parseInt(input.nextLine());
            System.out.print("Enter new Genre Name: ");
            String newGenreName = input.nextLine();

            try {
                PreparedStatement stmt = conn.prepareStatement("UPDATE Genre SET GenreName = ? WHERE GenreID = ?");
                stmt.setString(1, newGenreName);
                stmt.setInt(2, genreId);
                int affectedRows = stmt.executeUpdate();
                if (affectedRows > 0) {
                    System.out.println("Genre updated successfully!");
                } else {
                    System.out.println("No genre found with the specified ID.");
                }
            } catch (SQLException e) {
                System.out.println("Error updating genre: " + e.getMessage());
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a valid genre ID.");
        }
    }
}
