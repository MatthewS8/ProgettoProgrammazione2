import java.util.Iterator;
import java.util.List;

public interface DataBoard <E extends Data>{
    //Crea una categoria di dati se vengono rispettati i controlli di identità
     public void createCategory(String category, String passw)throws InvalidOperationException, IllegalArgumentException;

    // Rimuove  una categoria di dati se vengono rispettati i controlli di identità
     public void removeCategory(String category, String passw)throws InvalidOperationException, IllegalArgumentException;

    // Aggiunge un amico ad una categoria di dati se vengono rispettati i controlli di identità
     public void addFriend(String category, String passw, String friend)throws InvalidOperationException;

    // Rimuove un amico dauna categoria di dati se vengono rispettati i controlli di identità
     public void removeFriend(String category, String passw, String friend) throws InvalidOperationException;

    // Inserisce un dato in bacheca se vengono rispettati i controlli di identità
    public boolean put(String passw, E dato, String categoria) throws InvalidOperationException;

    // Restituisce una copia del dato in bacheca se vengono rispettati i controlli di identità
    public E get(String passw, E dato) throws InvalidOperationException, IllegalArgumentException;

    // Rimuove il dato dalla bacheca se vengono rispettati i controlli di identità
    public E remove(String passw, E dato) throws InvalidOperationException;

    //Crea la lista dei dati in bacheca di una determinata categoria se vengono rispettati i controlli di identità
    public List<E> getDataCategory(String passw, String category) throws InvalidOperationException;

    // Aggiunge un like a un dato se vengono rispettati i controlli di identità
     void insertLike(String friend, E dato) throws UserAlreadyExistsException, InvalidOperationException;

     // Restituisce un iteratore (senza remove) che genera tutti i dati in bacheca ordinati
     // risptto al numero di like se vengono rispettati i controlli di identità
    public Iterator<E> getIterator(String passw)throws InvalidOperationException;

    //Restituisce un iteratore (senza remove) che genera tutti i dati in
    // bacheca condivisi
     public Iterator<E> getFriendIterator(String friend);
}
