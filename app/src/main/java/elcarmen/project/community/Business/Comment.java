package elcarmen.project.community.Business;

/**
 * Created by alvaroramirez on 9/19/18.
 */

public class Comment {
    private int id;
    private int id_news;
    private int id_user;
    private String description;

    public Comment(int id, int id_news, int id_user, String description) {
        this.id = id;
        this.id_news = id_news;
        this.id_user = id_user;
        this.description = description;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_news() {
        return id_news;
    }

    public void setId_news(int id_news) {
        this.id_news = id_news;
    }

    public int getId_user() {
        return id_user;
    }

    public void setId_user(int id_user) {
        this.id_user = id_user;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


}
