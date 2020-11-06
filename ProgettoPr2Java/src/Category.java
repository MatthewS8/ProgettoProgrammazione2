import java.util.*;

public class Category <E extends Data> {
    /*OVERVIEW: classe di appoggio per strutture di Board.java
     * friends insieme di amici che possono accedere a this
     * arrData lista di dati presenti in una categoria
     */
    private final String name_category;
    private HashSet<String> friends;
    private ArrayList<E> arrData;

    public Category(String name){
        this(name, null, null);
    }

    public Category(String name, String friend, E data0){
        if(name == null) throw new IllegalArgumentException();
        this.name_category = name;
        //friends e' null
        friends = new HashSet<String>();
        if (friend != null)
            friends.add(friend); //ritorna true se inserito davvero
        this.arrData = new ArrayList<E>();
        if(data0 != null)
            arrData.add(data0);
    }

    public String getCategory(){
        //throws NullPointerException
        return name_category;
    }

    public boolean addFriend(String fname){
        return friends.add(fname); //return true if fname really added
    }

    public boolean removeFriend(String fname){
        return friends.remove(fname);
    }


    public boolean addData(E dato) {
        return arrData.add(dato);
    }

    public E searchFromData(E data){
        if(arrData.contains(data)){
            return arrData.get(arrData.indexOf(data));
        }
        return null;
    }

    public E removeData (E data){
        if(arrData.contains(data))
            return arrData.remove(arrData.indexOf(data));
        return null;
    }

    public ArrayList<E> getContent(){
        return new ArrayList<E>(arrData);
    }

    public void addLike(String fname, E content) throws UserAlreadyExistsException, WrongCategoryException{
        if(fname != null && !fname.equals("")) {
            if(this.isAFriend(fname)) {
                E dat = searchFromData(content); //gestisce se content è null o se non è presente nell'array
                if (dat != null) dat.incLikes(fname);
                else throw new WrongCategoryException("Content not found");
            }else throw new WrongCategoryException("Content not found");
        }else throw new IllegalArgumentException("String must be non null or void");
    }

    public boolean isAFriend(String fname){
        if(fname != null && !fname.equals(""))
            return friends.contains(fname);
        else throw new IllegalArgumentException("String must be non null or void");
    }

    @Override
    public boolean equals(Object obj){
        return this.name_category.equals(((Category<E>)obj).getCategory());
    }

}

class WrongCategoryException extends Exception{
    WrongCategoryException(){super();}
    WrongCategoryException(String s){super(s);}
}