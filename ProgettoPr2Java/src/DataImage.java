import java.util.ArrayList;

public class DataImage extends Data {
    private final String title;
    private int height, width, likes;
    private ArrayList<String> friendsLikes;

    public DataImage (String title0){
        this(title0, 100, 100);
    }

    public DataImage(String title0, int height0, int width0){
        this.title = title0;
        height = height0;
        width = width0;
    }
    @Override
    public void incLikes(String who) throws UserAlreadyExistsException{
        if(who != null && !who.equals("")){
            if(friendsLikes == null) friendsLikes = new ArrayList<>();
            if(friendsLikes.contains(who)) throw new UserAlreadyExistsException("User already liked this post");
            else {
                friendsLikes.add(who);
                likes++;
            }
        }else throw new IllegalArgumentException("User Not Valid");
    }

    public int getLikes(){
        return likes;
    }

    @Override
    public String getTitle(){
        return this.title;
    }

    @Override
    public void display() {
        if(likes>0)
            System.out.println("Showing: " + title + " with " + likes +" likes from " + friendsLikes);
        else
            System.out.println("Showing: " + title + " with " + likes +" likes");

    }
    @Override
    public boolean equals(Object o){
        return this.equals((DataImage) o);
    }
    public boolean equals(DataImage o){return this.title.equals(o.title);
    }

}
