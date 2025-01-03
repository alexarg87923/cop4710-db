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
        System.out.print("Enter Genre Name (or 'cancel' to exit): ");
        String genreName = input.nextLine();

        if (genreName.equalsIgnoreCase("cancel")) {
            return;
        }

        try {
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO Genre (GenreName) VALUES (?)");
            stmt.setString(1, genreName);
            stmt.executeUpdate();
            conn.commit();  // Committing the transaction
            System.out.println("Genre added successfully!");
        } catch (SQLException e) {
            System.out.println("Failed to add genre: " + e.getMessage());
            try {
                conn.rollback();  // Rolling back in case of error
            } catch (SQLException ex) {
                System.out.println("Error during rollback: " + ex.getMessage());
            }
        }
    }

    public void removeGenre() {
        System.out.println("\n======== Remove Genre ========");
        System.out.print("Enter Genre ID to remove or type 'list' to view all genres (or 'cancel' to exit): ");
        String genreInput = input.nextLine();

        if (genreInput.equalsIgnoreCase("list")) {
            listGenres();
            System.out.print("Enter Genre ID to remove: ");
            genreInput = input.nextLine();
        } else if (genreInput.equalsIgnoreCase("cancel")) {
            return;
        }

        try {
            int genreId = Integer.parseInt(genreInput);
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM Genre WHERE GenreID = ?");
            stmt.setInt(1, genreId);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                conn.commit();  // Committing the transaction
                System.out.println("Genre removed successfully!");
            } else {
                System.out.println("No genre found with the specified ID.");
            }
        } catch (SQLException | NumberFormatException e) {
            System.out.println("Error removing genre: " + e.getMessage());
            try {
                conn.rollback();  // Rolling back in case of error
            } catch (SQLException ex) {
                System.out.println("Error during rollback: " + ex.getMessage());
            }
        }
    }

    public void editGenre() {
        System.out.println("\n======== Edit Genre ========");
        System.out.print("Enter Genre ID to edit or type 'list' to view all genres (or 'cancel' to exit): ");
        String genreInput = input.nextLine();

        if (genreInput.equalsIgnoreCase("list")) {
            listGenres();
            System.out.print("Enter Genre ID: ");
            genreInput = input.nextLine();
        } else if (genreInput.equalsIgnoreCase("cancel")) {
            return;
        }

        try {
            int genreId = Integer.parseInt(genreInput);
            System.out.print("Enter new Genre Name (or 'cancel' to exit): ");
            String newGenreName = input.nextLine();

            if (newGenreName.equalsIgnoreCase("cancel")) {
                return;
            }

            PreparedStatement stmt = conn.prepareStatement("UPDATE Genre SET GenreName = ? WHERE GenreID = ?");
            stmt.setString(1, newGenreName);
            stmt.setInt(2, genreId);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                conn.commit();  // Committing the transaction
                System.out.println("Genre updated successfully!");
            } else {
                System.out.println("No genre found with the specified ID.");
            }
        } catch (SQLException | NumberFormatException e) {
            System.out.println("Error updating genre: " + e.getMessage());
            try {
                conn.rollback();  // Rolling back in case of error
            } catch (SQLException ex) {
                System.out.println("Error during rollback: " + ex.getMessage());
            }
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
}
