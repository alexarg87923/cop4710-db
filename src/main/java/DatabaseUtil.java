import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseUtil {
    private static final String URL = "jdbc:postgresql://149.28.106.167:5432/lim";
    private static final String USER = "cop4710";
    private static final String PASSWORD = "yMJ6zenikfum@3a";

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
