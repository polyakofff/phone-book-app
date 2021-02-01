import org.apache.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ContactDaoImpl<T> implements ContactDao<T> {

    static final Logger logger = Logger.getLogger(ContactDaoImpl.class);

    @Override
    public T addContact(Contact<T> contact) {
        logger.info("addContact query");
        T generatedId = null;
        try (Connection conn = ConnectionFactory.getConnection()) {
            String query = "INSERT INTO contacts (name, surname, patronymic, address, numbers, dateOfBirth, email) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, contact.getName());
            stmt.setString(2, contact.getSurname());
            stmt.setString(3, contact.getPatronymic());
            stmt.setString(4, contact.getAddress());
            stmt.setArray(5, conn.createArrayOf("VARCHAR(255)", contact.getNumbers().toArray()));
            stmt.setString(6, contact.getDateOfBirth());
            stmt.setString(7, contact.getEmail());
            int rows = stmt.executeUpdate();
            if (rows == 0) {
                throw new SQLException("failed to insert");
            }
            ResultSet result = stmt.getGeneratedKeys();
            while (result.next()) {
                generatedId = (T) result.getObject(1);
                contact.setId(generatedId);
            }
            logger.info("contact added with id=" + generatedId);

        } catch (SQLException ex) {
//            ex.printStackTrace();
            logger.error(ex);
        }
        return generatedId;
    }

    @Override
    public void deleteContact(T id) {
        logger.info("deleteContact query");
        try (Connection conn = ConnectionFactory.getConnection()) {
            String query = "DELETE FROM contacts WHERE id = (?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setObject(1, id);
            int rows = stmt.executeUpdate();
            if (rows == 0) {
                throw new SQLException("failed to delete");
            }
            logger.info("contact with id=" + id + " deleted");

        } catch (SQLException ex) {
//            ex.printStackTrace();
            logger.error(ex);
        }
    }

    /**
     * Knuth-Morris-Pratt algorithm for searching string S in text T as substring
     * both S and T should not contain '$'
     * @param str
     * @param substr
     * @return
     */
    private boolean occursInStr(String str, String substr) {
        String t = substr + "$" + str;
        int n = t.length();
        int[] p = new int[n];
        for (int i = 1; i < n; i++) {
            int j = p[i - 1];
            while (j > 0 && t.charAt(i) != t.charAt(j)) {
                j = p[j - 1];
            }
            if (t.charAt(i) == t.charAt(j))
                j++;
            p[i] = j;
            if (p[i] == substr.length())
                return true;
        }
        return false;
    }

    @Override
    public List<Contact<T>> searchBySubstr(String substr) {
        logger.info("searchBySubstr query");
        String substrUC = substr.toUpperCase();
        List<Contact<T>> occurrences = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection()) {
            String query = "SELECT * FROM contacts";
            ResultSet result = conn.prepareStatement(query).executeQuery();
            while (result.next()) {
                Contact<T> contact = new Contact<>();
                contact.setId((T) result.getObject(1));
                contact.setName(result.getString(2));
                contact.setSurname(result.getString(3));
                contact.setPatronymic(result.getString(4));
                contact.setAddress(result.getString(5));
                Object[] numbers = (Object[]) result.getArray(6).getArray();
                contact.setNumbers(Arrays.stream(numbers).map(String::valueOf).collect(Collectors.toList()));
                contact.setDateOfBirth(result.getString(7));
                contact.setEmail(result.getString(8));
                if (occursInStr(contact.getName().toUpperCase(), substrUC) ||
                        occursInStr(contact.getSurname().toUpperCase(), substrUC) ||
                        occursInStr(contact.getPatronymic().toUpperCase(), substrUC) ||
                        occursInStr(contact.getAddress().toUpperCase(), substrUC) ||
                        occursInStr(contact.getDateOfBirth().toUpperCase(), substrUC) ||
                        occursInStr(contact.getEmail().toUpperCase(), substrUC)) {
                    occurrences.add(contact);
                    continue;
                }
                for (String number : contact.getNumbers()) {
                    if (occursInStr(number.toUpperCase(), substrUC)) {
                        occurrences.add(contact);
                        break;
                    }
                }
            }

        } catch (SQLException ex) {
//            ex.printStackTrace();
            logger.error(ex);
        }
        return occurrences;
    }

    @Override
    public List<Contact<T>> getAllContacts() {
        logger.info("getAllContacts query");
        List<Contact<T>> allContacts = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection()) {
            String query = "SELECT * FROM contacts";
            ResultSet result = conn.prepareStatement(query).executeQuery();
            while (result.next()) {
                Contact<T> contact = new Contact<>();
                contact.setId((T) result.getObject(1));
                contact.setName(result.getString(2));
                contact.setSurname(result.getString(3));
                contact.setPatronymic(result.getString(4));
                contact.setAddress(result.getString(5));
                Object[] numbers = (Object[]) result.getArray(6).getArray();
                contact.setNumbers(Arrays.stream(numbers).map(String::valueOf).collect(Collectors.toList()));
                contact.setDateOfBirth(result.getString(7));
                contact.setEmail(result.getString(8));
                allContacts.add(contact);
            }

        } catch (SQLException ex) {
//            ex.printStackTrace();
            logger.error(ex);
        }
        return allContacts;
    }
}
