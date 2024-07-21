import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseUtil {
    private static final String URL = "jdbc:postgresql://149.28.106.167:5432/lim";
    private static final String USER = "cop4710";
    private static final String PASSWORD = "yMJ6zenikfum@3a";

    public static Connection connect() throws SQLException {
		Connection conn = null;
        try {
			conn = DriverManager.getConnection(URL, USER, PASSWORD);
			conn.setAutoCommit(false);
            System.out.println("Connected to the PostgreSQL server successfully.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
		return conn;
    }
}
