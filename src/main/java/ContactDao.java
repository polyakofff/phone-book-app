import java.util.List;

public interface ContactDao<T> {
    T addContact(Contact<T> contact);
    void deleteContact(T id);
    List<Contact<T>> searchBySubstr(String substr);
    List<Contact<T>> getAllContacts();
}
