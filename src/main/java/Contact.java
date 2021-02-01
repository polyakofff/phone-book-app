import java.util.Arrays;
import java.util.List;

public class Contact<T> {
    private T id;
    private String name;
    private String surname;
    private String patronymic;
    private String address;
    private List<String> numbers;
    private String dateOfBirth;
    private String email;

    public Contact() {
    }

    public Contact(String name, String surname, String patronymic, String address, List<String> numbers, String dateOfBirth, String email) {
        this.name = name;
        this.surname = surname;
        this.patronymic = patronymic;
        this.address = address;
        this.numbers = numbers;
        this.dateOfBirth = dateOfBirth;
        this.email = email;
    }

    public T getId() {
        return id;
    }

    public void setId(T id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPatronymic() {
        return patronymic;
    }

    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<String> getNumbers() {
        return numbers;
    }

    public void setNumbers(List<String> numbers) {
        this.numbers = numbers;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "Contact{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", patronymic='" + patronymic + '\'' +
                ", address='" + address + '\'' +
                ", numbers=" + Arrays.toString(numbers.toArray()) +
                ", dateOfBirth='" + dateOfBirth + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
