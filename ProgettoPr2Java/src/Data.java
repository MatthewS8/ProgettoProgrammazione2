public abstract class Data <E extends Data>{
    private String title;
    private int likes = 0;
    public abstract void display();

    abstract void incLikes(String who) throws UserAlreadyExistsException;
    String getTitle(){
        return title;
    }
    int getLikes(){
        return likes;
    }
}
