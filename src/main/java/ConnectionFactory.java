import org.h2.Driver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {

    private static final String URL = "jdbc:h2:./db/my_db2";

    public static Connection getConnection() {
        try {
            DriverManager.registerDriver(new Driver());
            return DriverManager.getConnection(URL);
        } catch (SQLException ex) {
            throw new RuntimeException("bad connection", ex);
        }
    }
}
