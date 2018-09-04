package elcarmen.project.community.Business;

import android.graphics.Bitmap;

import java.util.Date;

public class News {

    private String id;
    private String title;
    private String description;
    private Date date;
    private String url_photo;
    private Bitmap photo;
    private boolean approved;

    public News(String id, String title, String description, Date date, String url_photo, Bitmap photo, boolean approved) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.date = date;
        this.url_photo = url_photo;
        this.photo = photo;
        this.approved = approved;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getUrl_photo() {
        return url_photo;
    }

    public void setUrl_photo(String url_photo) {
        this.url_photo = url_photo;
    }

    public Bitmap getPhoto() {
        return photo;
    }

    public void setPhoto(Bitmap photo) {
        this.photo = photo;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }
}
