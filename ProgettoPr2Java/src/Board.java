import java.util.*;
import java.lang.*;

public class Board <E extends Data> implements DataBoard<E>{
    /*OVERVIEW: La classe Board è una collezione omogenea e modificabile di dati
     * di tipo E extends Data. La collezione è pensata per dividere i dati in categorie
     * e permettere l'accesso di amici solo a determinate categorie.
     * Alla Board è associato l'utente proprietario della board.
     * Un tipico valore di Board è:
     * <{nc1 ,{fc1, ..., fcN},{d1,... dN}}, u1> dove nc1 diverso da ogni ncI &&
     * fci != fcj sse fci.getCategory() == fcj.getCategory() &&
     * di != dn sse di.getcategory() == dj.getcategory
     */

    private HashSet<Category<E>> category = null; //{NameCAtegory,{friendsInCategory},{DataInCategory}}
    private final User user; //UserOwner di this

    /*IR(c): c.category != null && c.user != null &&
     * if(!c.category.isEmpty() && c.category.size()>=2) forall i, 0<= i < c.category.size() ,
     * forall j, 0 <= j < c.category.size(), i != j ->
     * c.category.getCategory(i) != c.category.getCategory(j) //ogni categoria è unica per istanza di board
     * && user unico per qualsiasi istanza
     */

    /* AF(c): f:C->{d1,..., dN} U {null} t.c. f(u.pass, c.getCategory(k) -> DatiinseritiInCategory
     * sse u.auth(u.pass) == true && category.contains(category_k) == true;
     */

    //Costruttore
    public Board(String uname, String upassw) throws UserAlreadyExistsException{
        /* REQUIRES: username e password != null e diverse dalla stringa vuota
         * EFFECTS: Instanzia l-insieme delle categorie di dato E e verifica la validit' del nuovo user
         *  L'username deve essere unico per ogni utente altrimenti lancia UserAlreadyExistsException;
         * MODIFIES: this.user, crea una collezione di categorie {};
         * THROWS: UserAlreadyExistsException
         */
        this.user = new User(uname, upassw);
        category = new HashSet<>();
    }


    @Override
    public void createCategory(String category0, String passw) throws InvalidOperationException, IllegalArgumentException{
        /* REQUIRES: String category != null && string category != "" && password valida
         * EFFECTS: Crea una nuova categoria altrimenti, se già presente lancia InvalidOperationException
         * MODIFIES: this.category
         * THROWS: InvalidOperationException if passw != this.user.getpassw() or if Category already exists,
         *   IllegalArgumentException if argument passed are null;
         */
        if(this.category == null) throw new InvalidOperationException("Board not Initialized");
        if(category0 != null && !category0.equals("")) {
            if (!user.auth(passw)) throw new InvalidOperationException("Password not valid");
            for(Category<E> ca : category )
                if(ca.equals(new Category<>(category0)))
                    throw new InvalidOperationException("Category already exists");
            this.category.add(new Category<>(category0));
        } else
            throw new IllegalArgumentException("Null argument passed");
    }

    @Override
    public void removeCategory(String category0, String passw)throws InvalidOperationException, IllegalArgumentException {
        /* REQUIRES: String category != null && string category != "" && password valida
         * EFFECTS: Rimuove una categoria se presente altrimenti non fa nulla;
         * MODIFIES: this.category
         * THROWS: InvalidOperationException if passw != this.user.getpassw()
         *   IllegalArgumentException if argument passed are null;
         */
        if(category0 != null && !category0.equals("")) {
            if (!user.auth(passw)) throw new InvalidOperationException("Password not valid");
            category.removeIf(eCategory -> eCategory.getCategory().equals(category0));

        }else
            throw new IllegalArgumentException("Null argument passed");
    }

    @Override
    public void addFriend(String category0, String passw, String friend) throws IllegalArgumentException, InvalidOperationException{
        /* REQUIRES: String category != null && string category != "" && password valida && String friend != null && String friend != ""
         * EFFECTS: Se la categoria esiste, aggiunge un nuovo amico ad una categoria se non è presente, altrimenti lancia InvalidOperationException
         * MODIFIES: this.category(category0).friends
         * THROWS: InvalidOperationException if passw != this.user.getpassw() or if Category not exists,
         *   IllegalArgumentException if argument passed are null;
         */
        if(category0 != null && !category0.equals("") && friend != null && !friend.equals("")){
            if(!user.auth(passw)) throw new InvalidOperationException("Password not valid");
            Category<E> ca = findCat(category0);
            if(ca != null) ca.addFriend(friend);
            else throw new InvalidOperationException("Category not found");
        } else
            throw new IllegalArgumentException("Null argument passed");
    }

    @Override
    public void removeFriend(String category0, String passw, String friend) throws IllegalArgumentException, InvalidOperationException {
        /* REQUIRES: String category != null && string category != "" && password valida && String friend != null && String friend != ""
         * EFFECTS: Se la categoria esiste, rimuove un nuovo amico da una categoria se è presente, altrimenti lancia InvalidOperationException
         * MODIFIES: this.category(category0).friends
         * THROWS: InvalidOperationException if passw != this.user.getpassw() or if Category not exists,
         *   IllegalArgumentException if argument passed are null;
         */
        if(category0 != null && !category0.equals("") && friend != null && !friend.equals("")){
            if(!user.auth(passw)) throw new InvalidOperationException("Password not valid");
            Category<E> ca = findCat(category0);
            if(ca != null) ca.removeFriend(friend);
            else throw new InvalidOperationException("Category not found");
        } else
            throw new IllegalArgumentException("Null argument passed");
    }

    @Override
    public boolean put(String passw, E dato, String categoria) throws InvalidOperationException {
        /* REQUIRES: String category != null && string category != "" && password valida
         * EFFECTS: Se la categoria esiste, aggiunge un nuovo dato alla categoria e ritorna true se effettivamente inserito false altrimenti,
         *  se la categoria non esiste lancia InvalidOperationException
         * MODIFIES: this.category(categoria).datas
         * THROWS: InvalidOperationException if passw != this.user.getpassw() or if Category not exists,
         *   IllegalArgumentException if argument passed are null;
         */
        if(categoria != null && !categoria.equals("") && dato != null){
            if(!user.auth(passw)) throw new InvalidOperationException("Password not valid");
            Category<E> sc = findCat(categoria);
            if(sc != null) return sc.addData(dato);
            else throw new InvalidOperationException("Category not found");
        } else throw new IllegalArgumentException("Null argument passed");
    }

    @Override
    public E get(String passw, E dato) throws InvalidOperationException, IllegalArgumentException {
        /* REQUIRES: dato != null && password valida
         * EFFECTS: Se il dato è presente in categoria ritorna il valore del dato altrimenti ritorna null
         * MODIFIES: -
         * THROWS: InvalidOperationException if passw != this.user.getpassw()
         *   IllegalArgumentException if argument passed are null;
         */
        if(dato != null){
            if(!user.auth(passw)) throw new InvalidOperationException("Password not valid");
             // se sono l'owner del dato
            for( Category<E> cat : category) {
                E dato0 = cat.searchFromData(dato);
                if (dato0 != null) return dato0;
            }
        } else throw new IllegalArgumentException("Null argument passed");
        return null;
    }

    @Override
    public E remove(String passw, E dato) throws  InvalidOperationException{
        /* REQUIRES: dato != null && password valida
         * EFFECTS: Se il dato è presente in categoria ritorna il valore del dato e lo rimuove altrimenti lancia InvalidOperationException
         * MODIFIES: this.category(cat).datas
         * THROWS: InvalidOperationException if passw != this.user.getpassw() or if data not exists
         *   IllegalArgumentException if argument passed are null;
         */
        if(passw != null && dato != null){
            if(!user.auth(passw)) throw  new InvalidOperationException("Password not valid");
            for( Category<E> cat : category) {
                E dato0 = cat.removeData(dato);
                if (dato0 != null) return dato0;
            } throw new InvalidOperationException("Data not found");
        }else throw new IllegalArgumentException("Null argument passed");
    }

    @Override
    public List<E> getDataCategory(String passw, String category) throws InvalidOperationException {
        /* REQUIRES: String category != null && string category != "" && password valida
         * EFFECTS: Se category esiste ritorna una lista di dati (evetualmnete vuota)
         * MODIFIES: -
         * THROWS: InvalidOperationException if passw != this.user.getpassw()
         *   IllegalArgumentException if argument passed are null;
         */
        if(passw != null && category != null) {
            if (!user.auth(passw)) throw new InvalidOperationException("Password not valid");
            for(Category<E> cat : this.category){
                if(cat.getCategory().equals(category)) return cat.getContent();
            }
        }else throw new IllegalArgumentException("Null argument passed");
        return new ArrayList<>();
    }

    @Override
    public void insertLike(String friend, E dato) throws UserAlreadyExistsException{
        /* REQUIRES: String friend != null && String friend != "" && dato != null && password valida
         * EFFECTS: Se il dato è presente in categoria incrementa il contatore di like associato al dato e inserisce friend in un array specifico
         *   se friend ha già messo like al post lancia UserAlreadyExistsException
         * MODIFIES: this.category(cat).datas
         * THROWS: IllegalArgumentException if argument passed are null
         *   UserAlreadyExistsException if friend already liked dato
         */
        if(friend != null && !friend.equals("") && dato!=null) {
            for(Category<E> cat : this.category){
                try {
                    cat.addLike(friend, dato);
                    return;
                }catch (WrongCategoryException e){;}
            }
        }else throw new IllegalArgumentException("Null argument passed");
    }

    @Override
    public Iterator<E> getIterator(String passw) throws InvalidOperationException{
        /* REQUIRES:password valida
         * EFFECTS: Ritorna un iteratore che scorre una lista non modificabile di tutti gli elementi
         *  ordinati per like a prescidenre dalla categoria di appartenenza
         * (possono presentarsi duplicati se il dato viene inserito in più categorie)
         * MODIFIES: -
         * THROWS: InvalidOperationException if passw != this.user.getpassw()
         *   IllegalArgumentException if argument passed are null;
         */
        if(passw != null && !passw.equals("")) {
            if (!user.auth(passw)) throw new InvalidOperationException("Wrong Password");
            Iterator<Category<E>> allElem = category.iterator();
            ArrayList<E> allElements = new ArrayList<>();
            while (allElem.hasNext()) {
                allElements.addAll(allElem.next().getContent());
            }
            allElements.sort((e1, e2) -> e2.getLikes() - e1.getLikes());
            return Collections.unmodifiableList(allElements).iterator();
        }
        throw new IllegalArgumentException("Null argument passed");
    }

    @Override
    public Iterator<E> getFriendIterator(String friend) {
        /* REQUIRES: friend != null && friend != ""
         * EFFECTS: Ritorna un iteratore che scorre una lista di tutti gli elementi condivisi con friend
         * a prescidenre dalla categoria di appartenenza
         * (possono presentarsi duplicati se il dato viene inserito in più categorie e friend ha accesso a queste)
         * MODIFIES: -
         * THROWS: InvalidOperationException if passw != this.user.getpassw()
         *   IllegalArgumentException if argument passed are null;
         */
        Iterator<Category<E>> allCat = category.iterator();
        HashSet<E> visEle = new HashSet<>();
        while(allCat.hasNext()){
            Category<E> cat = allCat.next();
            if(cat.isAFriend(friend)) visEle.addAll(cat.getContent());
        }
        return visEle.iterator();
    }

    private Category<E> findCat(String cat){
        for(Category<E> sc : this.category){
            if(sc.getCategory().equals(cat))
                return sc;
        }
        return null;
    }
}


class InvalidOperationException extends Exception{
    InvalidOperationException(){
        super();
    }

    InvalidOperationException(String s){
        super(s);
    }
}

class User {
    //OVERVIEW: inner class che gestisce la parte di unicità dell'utente e verifica che le credenziali siano corrette
    private final String name;
    private final String passw;
    private static HashSet<String> signedUsers;

    User(String name0, String passw0) throws IllegalArgumentException, UserAlreadyExistsException {
        if (name0 != null && !name0.equals("") && passw0 != null && !passw0.equals("")) {
            if (signedUsers == null) {
                signedUsers = new HashSet<>(); //inizializzazione
            }
            if (isSigned(name0)) throw new UserAlreadyExistsException(name0);
            this.name = name0;
            this.passw = passw0;
            signedUsers.add(name);
        } else
            throw new IllegalArgumentException("Parameters must be non-null");
    }

    String getName() {
        return new String(name);
    }
    String getPassw(){
        return new String(passw);
    }

    boolean auth(String passw0){
        if(passw0 == null) throw new IllegalArgumentException("Password must be non null");
        return this.passw.equals(passw0);
    }

    boolean isSigned(String name0) throws IllegalArgumentException{
        if(name0 == null) throw new IllegalArgumentException();
        return signedUsers.contains(name0);
    }

}

class UserAlreadyExistsException extends Exception{
    UserAlreadyExistsException(){ super(); }
    UserAlreadyExistsException(String s){ super(s); }
}

