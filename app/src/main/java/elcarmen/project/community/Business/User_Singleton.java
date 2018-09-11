package elcarmen.project.community.Business;

import android.graphics.Bitmap;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class User_Singleton {
    private static final User_Singleton ourInstance = new User_Singleton();

    private int id;
    private String name;
    private String email;
    private String tel;
    private String cel;
    private String address;
    private String url_photo;
    private String url_photo_rounded;
    private Bitmap photo;
    private Bitmap photo_rounded;
    private boolean privateProfile;
    private String auth_token;
    private ArrayList<Integer> communities_admin = new ArrayList<Integer>();
    private ArrayList<Integer> communities_member = new ArrayList<Integer>();
    //private ArrayList<Notification> notifications = new ArrayList<Notification>();
    private ArrayList<News> news = new ArrayList<News>();

    public static User_Singleton getInstance() {
        return ourInstance;
    }

    private User_Singleton() {
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getCel() {
        return cel;
    }

    public void setCel(String cel) {
        this.cel = cel;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public String getAuth_token() {
        return auth_token;
    }

    public void setAuth_token(String auth_token) {
        this.auth_token = auth_token;
    }

    public ArrayList<Integer> getCommunities_admin() {
        return communities_admin;
    }

    public void setCommunities_admin(JSONArray communities_admin) {
        for(int i = 0; i < communities_admin.length(); i++){
            try {
                this.communities_admin.add(communities_admin.getInt(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void addCommunity_admin(int idCommunity){
        communities_admin.add(idCommunity);
    }

    public ArrayList<Integer> getCommunities_member() {
        return communities_member;
    }

    public void setCommunities_member(ArrayList<Integer> communities_member) {
        this.communities_member = communities_member;
    }

    public boolean isAdmin(int idCommunity){
        for(Integer idComm : communities_admin){
            if(idComm.intValue() == idCommunity){
                return true;
            }
        }
        return false;
    }

    public ArrayList<News> getNews() {
        return news;
    }

    public void setNews(ArrayList<News> news) {
        this.news = news;
    }

    public boolean isPrivateProfile() {
        return privateProfile;
    }

    public void setPrivateProfile(boolean privateProfile) {
        this.privateProfile = privateProfile;
    }
}
