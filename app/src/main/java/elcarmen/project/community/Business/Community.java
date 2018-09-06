package elcarmen.project.community.Business;

import android.graphics.Bitmap;

import java.util.ArrayList;

public class Community {

    private int id;
    private String name;
    private String description;
    private ArrayList<String> rules;
    private boolean subcommunity;
    private String url_photo;
    private String url_photo_rounded;
    private Bitmap photo;
    private Bitmap photo_rounded;
    private ArrayList<Integer> id_subcommunities;
    private ArrayList<News> news;
    //private ArrayList<Event> events;
    private ArrayList<User> members;
    private ArrayList<User> admins;

    public Community(int id, String name, String description, ArrayList<String> rules, boolean subcommunity, String url_photo, String url_photo_rounded, ArrayList<Integer> id_subcommunities) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.rules = rules;
        this.subcommunity = subcommunity;
        this.url_photo = url_photo;
        this.url_photo_rounded = url_photo_rounded;
        this.id_subcommunities = id_subcommunities;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<String> getRules() {
        return rules;
    }

    public void setRules(ArrayList<String> rules) {
        this.rules = rules;
    }

    public boolean isSubcommunity() {
        return subcommunity;
    }

    public void setSubcommunity(boolean subcommunity) {
        this.subcommunity = subcommunity;
    }

    public String getUrl_photo() {
        return url_photo;
    }

    public void setUrl_photo(String url_photo) {
        this.url_photo = url_photo;
    }

    public String getUrl_photo_rounded() {
        return url_photo_rounded;
    }

    public void setUrl_photo_rounded(String url_photo_rounded) {
        this.url_photo_rounded = url_photo_rounded;
    }

    public Bitmap getPhoto() {
        return photo;
    }

    public void setPhoto(Bitmap photo) {
        this.photo = photo;
    }

    public Bitmap getPhoto_rounded() {
        return photo_rounded;
    }

    public void setPhoto_rounded(Bitmap photo_rounded) {
        this.photo_rounded = photo_rounded;
    }

    public ArrayList<Integer> getId_subcommunities() {
        return id_subcommunities;
    }

    public void setId_subcommunities(ArrayList<Integer> id_subcommunities) {
        this.id_subcommunities = id_subcommunities;
    }

    public ArrayList<News> getNews() {
        return news;
    }

    public void setNews(ArrayList<News> news) {
        this.news = news;
    }

    public ArrayList<User> getMembers() {
        return members;
    }

    public void setMembers(ArrayList<User> members) {
        this.members = members;
    }

    public ArrayList<User> getAdmins() {
        return admins;
    }

    public void setAdmins(ArrayList<User> admins) {
        this.admins = admins;
    }
}
