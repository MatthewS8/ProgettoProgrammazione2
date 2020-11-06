import java.util.ArrayList;

public class VideoData extends Data{
    private final String title;
    int length, likes;
    private ArrayList<String> friendsLikes;

    public VideoData (String title0, int length0){
        this.title = title0;
        this.length = length0;
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
        return this.equals((VideoData) o);
    }
    public boolean equals(VideoData o){return this.title.equals(o.title);
    }
}
