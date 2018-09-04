package elcarmen.project.community.Business;

import java.util.ArrayList;

public class User_Singleton {
    private static final User_Singleton ourInstance = new User_Singleton();

    private User user;
    private String auth_token;
    private ArrayList<String> communities_admin = new ArrayList<String>();
    private ArrayList<String> communities_member = new ArrayList<String>();
    //private ArrayList<Notification> notifications = new ArrayList<Notification>();
    private ArrayList<News> news = new ArrayList<News>();

    public static User_Singleton getInstance() {
        return ourInstance;
    }

    private User_Singleton() {
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getAuth_token() {
        return auth_token;
    }

    public void setAuth_token(String auth_token) {
        this.auth_token = auth_token;
    }

    public ArrayList<String> getCommunities_admin() {
        return communities_admin;
    }

    public void setCommunities_admin(ArrayList<String> communities_admin) {
        this.communities_admin = communities_admin;
    }

    public ArrayList<String> getCommunities_member() {
        return communities_member;
    }

    public void setCommunities_member(ArrayList<String> communities_member) {
        this.communities_member = communities_member;
    }

    public ArrayList<News> getNews() {
        return news;
    }

    public void setNews(ArrayList<News> news) {
        this.news = news;
    }
}
