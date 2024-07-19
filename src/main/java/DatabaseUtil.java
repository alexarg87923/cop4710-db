import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseUtil {
    private static final String URL = "jdbc:postgresql://149.28.106.167:5432/lim";
    private static final String USER = "cop4710";
    private static final String PASSWORD = "yMJ6zenikfum@3a";

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
	
	public static void initializeDatabase() {
    try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
         Statement stmt = conn.createStatement()) {

        String sql = new String(Files.readAllBytes(Paths.get("init_database.sql")));
        
        stmt.execute(sql);
        System.out.println("Database initialized successfully.");
    } catch (SQLException | IOException e) {
        System.out.println("Error initializing database: " + e.getMessage());
    }
}

}
