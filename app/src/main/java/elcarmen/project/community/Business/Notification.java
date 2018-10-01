package elcarmen.project.community.Business;

import android.graphics.Bitmap;

import java.util.ArrayList;

public class Notification {

    private int id;
    private int idUser;
    private int idContent;
    private boolean isNews;
    private boolean isReports;
    private boolean isEvents;
    private String titleContent;
    private boolean seen;
    private String photo;

    public Notification(int id, int idUser, int idContent, boolean isNews, boolean isReports, boolean isEvents, String titleContent, boolean seen, String photo) {
        this.id = id;
        this.idUser = idUser;
        this.idContent = idContent;
        this.isNews = isNews;
        this.isReports = isReports;
        this.isEvents = isEvents;
        this.titleContent = titleContent;
        this.seen = seen;
        this.photo = photo;
    }

    public Notification() {
    }

    public int getId() {
        return id;
    }


    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public int getIdContent() {
        return idContent;
    }

    public void setIdContent(int idContent) {
        this.idContent = idContent;
    }

    public boolean isNews() {
        return isNews;
    }

    public void setNews(boolean news) {
        isNews = news;
    }

    public boolean isReports() {
        return isReports;
    }

    public void setReports(boolean reports) {
        isReports = reports;
    }

    public boolean isEvents() {
        return isEvents;
    }

    public void setEvents(boolean events) {
        isEvents = events;
    }

    public String getTitleContent() {
        return titleContent;
    }

    public void setTitleContent(String titleContent) {
        this.titleContent = titleContent;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
