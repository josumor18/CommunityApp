package elcarmen.project.community.Business;

import java.io.Serializable;

public class Chat implements Serializable {
    private int id;
    private int id_community;
    private String community_name;
    private int id_receiver;
    private String name_receiver;
    private boolean visto;
    private String last_message;

    public Chat() {
    }

    public Chat(int id, int id_community, String community_name, int id_receiver, String name_receiver, boolean visto, String last_message) {
        this.id = id;
        this.id_community = id_community;
        this.community_name = community_name;
        this.id_receiver = id_receiver;
        this.name_receiver = name_receiver;
        this.visto = visto;
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

    public int getId_receiver() {
        return id_receiver;
    }

    public void setId_receiver(int id_receiver) {
        this.id_receiver = id_receiver;
    }

    public String getName_receiver() {
        return name_receiver;
    }

    public void setName_receiver(String name_receiver) {
        this.name_receiver = name_receiver;
    }

    public boolean isVisto() {
        return visto;
    }

    public void setVisto(boolean visto) {
        this.visto = visto;
    }

    public String getLast_message() {
        return last_message;
    }

    public void setLast_message(String last_message) {
        this.last_message = last_message;
    }
}
