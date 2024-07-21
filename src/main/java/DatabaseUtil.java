import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseUtil {
    private static final String URL = "jdbc:postgresql://149.28.106.167:5432/lim";

    public static Connection connect(String user, String password) throws SQLException {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL, user, password);
            conn.setAutoCommit(false);
            System.out.println("Connected to the PostgreSQL server successfully.");
        } catch (SQLException e) {
            System.out.println("Error in connection: " + e.getMessage());
        }
        return conn;
    }
}
