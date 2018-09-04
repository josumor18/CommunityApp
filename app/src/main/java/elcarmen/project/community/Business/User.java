package elcarmen.project.community.Business;

import android.graphics.Bitmap;

import java.util.ArrayList;

public class User {

    private String id;
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

    public User(String id, String name, String email, String tel, String cel, String address, String url_photo, String url_photo_rounded, Bitmap photo, Bitmap photo_rounded, boolean privateProfile) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.tel = tel;
        this.cel = cel;
        this.address = address;
        this.url_photo = url_photo;
        this.url_photo_rounded = url_photo_rounded;
        this.photo = photo;
        this.photo_rounded = photo_rounded;
        this.privateProfile = privateProfile;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public boolean isPrivateProfile() {
        return privateProfile;
    }

    public void setPrivateProfile(boolean privateProfile) {
        this.privateProfile = privateProfile;
    }
}
