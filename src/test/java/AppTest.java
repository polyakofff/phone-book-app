import org.h2.Driver;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.*;
import java.util.List;

public class AppTest {

    private Connection getConnection() {
        try {
            DriverManager.registerDriver(new Driver());
            return DriverManager.getConnection("jdbc:h2:mem:test_db");
        } catch (SQLException ex) {
            throw new RuntimeException("bad connection", ex);
        }
    }

    @Test
    public void testDatabase() {
        try (Connection conn = getConnection()) {
            {
                String createTableQuery = "CREATE TABLE contacts (" +
                        "    id INT PRIMARY KEY AUTO_INCREMENT," +
                        "    name VARCHAR(255)," +
                        "    surname VARCHAR(255)," +
                        "    patronymic VARCHAR(255)," +
                        "    address VARCHAR(255)," +
                        "    numbers ARRAY," +
                        "    dateOfBirth VARCHAR(255)," +
                        "    email VARCHAR(255)" +
                        ")";
                conn.createStatement().execute(createTableQuery);
            }

            {
                Contact<Integer> contact = new Contact<>("name",
                        "surname",
                        "patronymic",
                        "address",
                        List.of("number-1", "number-2"),
                        "01.01.0001",
                        "email");
                String insertContactQuery = "INSERT INTO contacts (name, surname, patronymic, address, numbers, dateOfBirth, email) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(insertContactQuery, Statement.RETURN_GENERATED_KEYS);
                stmt.setString(1, contact.getName());
                stmt.setString(2, contact.getSurname());
                stmt.setString(3, contact.getPatronymic());
                stmt.setString(4, contact.getAddress());
                stmt.setArray(5, conn.createArrayOf("VARCHAR(255)", contact.getNumbers().toArray()));
                stmt.setString(6, contact.getDateOfBirth());
                stmt.setString(7, contact.getEmail());
                int rows = stmt.executeUpdate();
                Assert.assertEquals(rows, 1);
                if (rows == 0) {
                    throw new SQLException("failed to insert");
                }
                ResultSet result = stmt.getGeneratedKeys();
                int generatedId = -1;
                while (result.next()) {
                    generatedId = result.getInt(1);
                    contact.setId(generatedId);
                }
                Assert.assertEquals(generatedId, 1);
            }

            {
                String deleteContactQuery = "DELETE FROM contacts WHERE id = (?)";
                PreparedStatement stmt = conn.prepareStatement(deleteContactQuery);
                stmt.setObject(1, 1);
                int rows = stmt.executeUpdate();
                Assert.assertEquals(rows, 1);
                if (rows == 0) {
                    throw new SQLException("failed to delete");
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
