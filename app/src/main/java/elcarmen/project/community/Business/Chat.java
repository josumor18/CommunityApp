package elcarmen.project.community.Business;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import elcarmen.project.community.R;

public class Chat {
    private int id;
    private int id_community;
    private String community_name;
    private boolean is_group;
    private int id_user;
    private String name_user;
    private Message last_message;

    public Chat() {
    }

    public Chat(int id, int id_community, String community_name, boolean is_group, int id_user, String name_user, Message last_message) {
        this.id = id;
        this.id_community = id_community;
        this.community_name = community_name;
        this.is_group = is_group;
        this.id_user = id_user;
        this.name_user = name_user;
        this.last_message = last_message;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_community() {
        return id_community;
    }

    public void setId_community(int id_community) {
        this.id_community = id_community;
    }

    public String getCommunity_name() {
        return community_name;
    }

    public void setCommunity_name(String community_name) {
        this.community_name = community_name;
    }

    public boolean isIs_group() {
        return is_group;
    }

    public void setIs_group(boolean is_group) {
        this.is_group = is_group;
    }

    public int getId_user() {
        return id_user;
    }

    public void setId_user(int id_user) {
        this.id_user = id_user;
    }

    public String getName_user() {
        return name_user;
    }

    public void setName_user(String name_user) {
        this.name_user = name_user;
    }

    public Message getLast_message() {
        return last_message;
    }

    public void setLast_message(Message last_message) {
        this.last_message = last_message;
    }
}
