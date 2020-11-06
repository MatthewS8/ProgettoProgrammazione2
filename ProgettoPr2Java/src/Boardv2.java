import java.util.*;

public class Boardv2 <E extends Data> implements DataBoard<E> {
    /*OVERVIEW: La classe Boardv2 è una collezione omogenea e modificabile di dati
     * di tipo E extends Data. La collezione è pensata per dividere i dati in categorie
     * Alla Boardv2 è associato l'utente proprietario della board.
     * Un tipico valore di Board è:
     * <{c1, {f1, ..., fN} }, {c1, {d1, ..., dN}}, u1> , <{cN, {f1, ..., fN} }, {cN, {d1, ..., dN}}, u1>
     * dove ogni cI diverso da ogni cJ con i != j &&
     * fI != fJ se friends.getKey(fI) == friends.getKey(fJ) &&
     * dI != dJ se Datas.getKey(dI) == Datas.getKey(dJ)
     */

    private HashMap<String,HashSet<String>> friends; // {Name_Category, {friendsInCategory}}
    private HashMap<String,TreeSet<E>> datas; //{Name_Category, {DataInCategory}}
    private final User me; //UserOwner di this

    public Boardv2(String username, String passw)throws UserAlreadyExistsException{
        this.me = new User(username, passw);
        this.friends = new HashMap<String, HashSet<String>>();
        this.datas = new HashMap<String,TreeSet<E>>();
    }

    /*IR(c): c.friends != null && c.datas != null && c.user != null &&
     *  friends.keYSet() == datas.keySet() &&
     * for all category in friends.getValue(category).friend[i] != friends.getValue(category).friend[i] for all 0<= i,j < friends.getValue(category).size(), i != j
     * for all category in datas.getValue(category).data[i] != datas.getValue(category).data[j] for all 0<= i,j < datas.getValue(category).size(), i != j
     * && user unico per qualsiasi istanza
     */

    /* AF(c): f:C->{ {f1,..., fN} U {}, {d1, ..., dN} U {null} } t.c. f(u.pass, category) -> { {AmiciInCategory}, {DatiinseritiInCategory} }
     * sse u.auth(u.pass) == true && friends.get(category) != null && datas.get(category) !=null
     */

    //Costruttore
    @Override
    public void createCategory(String category, String passw) throws InvalidOperationException, IllegalArgumentException {
        if(category != null && !category.equals("'")){
            if(!me.auth(passw)) throw new InvalidOperationException("Password mismatch");
            if(datas.containsKey(category) || friends.containsKey(category))
                throw new InvalidOperationException("Category already exists");
            else {
                this.friends.put(category, new HashSet<>());
                this.datas.put(category,new TreeSet<>( (e1 , e2) -> e1.getTitle().compareTo(e2.getTitle())));
            }
        }else throw new IllegalArgumentException("Parameters must be non-null");
    }

    @Override
    public void removeCategory(String category, String passw) throws InvalidOperationException, IllegalArgumentException {
        if(category != null && !category.equals("'")){
            if(!me.auth(passw)) throw new InvalidOperationException("Password mismatch");
            if(friends.remove(category) == null || datas.remove(category) == null)
                throw new InvalidOperationException("Category not found");
        }else throw new IllegalArgumentException("Parameters must be non-null");
    }

    @Override
    public void addFriend(String category, String passw, String friend) throws InvalidOperationException {
        if(category != null && !category.equals("'") && friend != null && !friend.equals("")){
            if(!me.auth(passw)) throw new InvalidOperationException("Password mismatch");
            if(!friends.containsKey(category)) throw  new InvalidOperationException("Category not found");
            HashSet<String> copy = friends.get(category);
            if(copy == null) copy = new HashSet<>();
            copy.add(friend);
            friends.put(category, copy);
        }else throw new IllegalArgumentException("Parameters must be non-null");
    }

    @Override
    public void removeFriend(String category, String passw, String friend) throws InvalidOperationException {
        if(category != null && !category.equals("'") && friend != null && !friend.equals("")){
            if(!me.auth(passw)) throw new InvalidOperationException("Password mismatch");
            if(!friends.containsKey(category)) throw  new InvalidOperationException("Category not found");
            HashSet<String> copy = friends.get(category);
            if(copy == null) copy = new HashSet<>();
            copy.remove(friend);
            friends.put(category, copy);
        }else throw new IllegalArgumentException("Parameters must be non-null");
    }

    @Override
    public boolean put(String passw, E dato, String category) throws InvalidOperationException {
        if(category != null && !category.equals("'") && dato != null) {
            if(!me.auth(passw)) throw new InvalidOperationException("Password mismatch");
            if(!datas.containsKey(category)) throw  new InvalidOperationException("Category not found");
            TreeSet<E> copy = datas.get(category);
            boolean res = copy.add(dato);
            res = res && (datas.put(category,copy) != null);
            return res;
        }else throw new IllegalArgumentException("Parameters must be non-null");
    }

    @Override
    public E get(String passw, E dato) throws InvalidOperationException, IllegalArgumentException {
        if(dato != null){
            if(!me.auth(passw)) throw new InvalidOperationException("Password mismatch");
//            Iterator<TreeSet<E>> cat = datas.values().iterator();
            for(TreeSet<E> cat : datas.values() ){
                if (cat.contains(dato) || dato.equals(cat.first())) {
                    for(Iterator<E> dat  = cat.iterator(); dat.hasNext(); ){
                        E retDato = dat.next();
                        if(retDato!= null && retDato.equals(dato)) return retDato;
                    }
                }
            } return null;
        }else throw new IllegalArgumentException("Parameters must be non-null");
    }

    @Override
    public E remove(String passw, E dato) throws InvalidOperationException {
        if(dato != null){
            if(!me.auth(passw)) throw new InvalidOperationException("Password mismatch");
            E retData = null;
            for (String category : datas.keySet()) {
                TreeSet<E> treeSet = datas.get(category);
                if(treeSet.contains(dato)){
                    Iterator<E> it = treeSet.iterator();
                    while(it.hasNext()){
                        E tmp = it.next();
                        if(tmp.equals(dato)){
                            retData = tmp;
                            it.remove();
                        }
                    }
                }
            } return retData;
        }else throw new IllegalArgumentException("Parameters must be non-null");
    }

    @Override
    public List<E> getDataCategory(String passw, String category) throws InvalidOperationException {
        if(category != null && !category.equals("'")) {
            if (!me.auth(passw)) throw new InvalidOperationException("Password mismatch");
            if(!datas.containsKey(category)) throw  new InvalidOperationException("Category not found");
            TreeSet <E> tree = datas.get(category);
            List<E> retList = new ArrayList<>(tree.size());
            retList.addAll(tree);
            return retList;
        }else throw new IllegalArgumentException("Parameters must be non-null");
    }

    @Override
    public void insertLike(String friend, E dato) throws UserAlreadyExistsException, InvalidOperationException {
        if(friend != null && !friend.equals("")) {
            boolean done = false;
            for(String category : datas.keySet()){
                TreeSet<E> t = datas.get(category);
                if(t.contains(dato)){
                    HashSet<String> fr = friends.get(category);
                    if(fr.contains(friend)){
                        Iterator<E> it = t.iterator();
                        while(it.hasNext() && !done) {
                            E dtl = it.next();
                            if(dtl != null && dtl.equals(dato)) {
                                dtl.incLikes(friend);
                                done = true;
                            }
                        }
                    }
                }
                if(done) return;
            } throw new InvalidOperationException("Data not found");
        }else throw new IllegalArgumentException("Parameters must be non-null");
    }

    @Override
    public Iterator<E> getIterator(String passw) throws InvalidOperationException {
        if (!me.auth(passw)) throw new InvalidOperationException("Password mismatch");
        ArrayList<E> ar = new ArrayList<>();
        for (Iterator<TreeSet<E>> it =datas.values().iterator(); it.hasNext(); )
            ar.addAll(it.next());
        ar.sort((e1, e2) -> e2.getLikes() - e1.getLikes());
        return Collections.unmodifiableList(ar).iterator();
    }

    @Override
    public Iterator<E> getFriendIterator(String friend) {
        if(friend != null && !friend.equals("")) {
            ArrayList<E> ar = new ArrayList<>();
            for(String category : friends.keySet()){
                HashSet<String> fr = friends.get(category);
                if(fr.contains(friend)){
                    TreeSet<E> tr = datas.get(category);
                    ar.addAll(tr);
                }
            }
            return ar.iterator();
        }else throw new IllegalArgumentException("Parameters must be non-null");
    }
}
