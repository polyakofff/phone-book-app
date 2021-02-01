import org.apache.log4j.Logger;

import java.sql.*;
import java.text.ParseException;
import java.util.*;

public class App {

    static final Logger logger = Logger.getLogger(App.class);

    static void createTableIfDoesNotExist(String tableName) {
        try (Connection conn = ConnectionFactory.getConnection()) {
            ResultSet tables = conn.createStatement().executeQuery("SHOW TABLES");
            boolean exists = false;
            while (tables.next()) {
                exists ^= tables.getString(1).equals(tableName.toUpperCase());
            }
            if (!exists) {
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

                logger.info("table 'contacts' created");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        createTableIfDoesNotExist("contacts");

        ContactDaoImpl<Integer> contactDao = new ContactDaoImpl<>();

        String hint = "you can enter the following commands starting with '/':\n" +
                "/add 'name' 'surname' 'patronymic' 'address' 'number1' 'numberN' 'date of birth' 'email'\n" +
                "/delete 'id'\n" +
                "/search 'substr'\n" +
                "/all";
        System.out.println(hint + "\n");

        Scanner scanner = new Scanner(System.in);
        while (true) {
            String command = scanner.nextLine();

            if (command.startsWith("/add '")) {
                String[] words = command.split("'");
                int n = words.length;
                if (n < 14) {
                    System.out.println(hint + "\n");
                    continue;
                }
                logger.debug("command='" + command + "'");
                String name = words[1];
                String surname = words[3];
                String patronymic = words[5];
                String address = words[7];
                List<String> numbers = new ArrayList<>();
                for (int i = 9; i < n - 3; i += 2) {
                    numbers.add(words[i]);
                }
                String dateOfBirth = words[n - 3];
                String email = words[n - 1];
                Contact<Integer> contact = new Contact<>(name, surname, patronymic, address, numbers, dateOfBirth, email);
                contactDao.addContact(contact);

            } else if (command.startsWith("/delete '")) {
                String[] words = command.split("'");int n = words.length;
                if (n != 2) {
                    System.out.println(hint + "\n");
                    continue;
                }
                logger.debug("command='" + command + "'");
                try {
                    Integer id = Integer.parseInt(words[1]);
                    contactDao.deleteContact(id);
                } catch (NumberFormatException ex) {
                    System.out.println("wrong id");
                }

            } else if (command.startsWith("/search '")) {
                String[] words = command.split("'");int n = words.length;
                if (n != 2) {
                    System.out.println(hint + "\n");
                    continue;
                }
                logger.debug("command='" + command + "'");
                List<Contact<Integer>> occurrences = contactDao.searchBySubstr(words[1]);
                System.out.println("{");
                for (Contact<Integer> contact : occurrences) {
                    System.out.println(contact);
                }
                System.out.println("}");

            } else if (command.equals("/all")) {
                logger.debug("command='" + command + "'");
                List<Contact<Integer>> allContacts = contactDao.getAllContacts();
                System.out.println("{");
                for (Contact<Integer> contact : allContacts) {
                    System.out.println(contact);
                }
                System.out.println("}");

            } else {
                System.out.println(hint);
            }
            System.out.println();
        }
    }
}
