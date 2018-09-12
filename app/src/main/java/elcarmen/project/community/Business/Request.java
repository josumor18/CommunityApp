package elcarmen.project.community.Business;

public class Request {
    private int id_community;
    private int id_user;
    private boolean seen;

    public Request(int id_community, int id_user, boolean seen) {
        this.id_community = id_community;
        this.id_user = id_user;
        this.seen = seen;
    }

    public int getId_community() {
        return id_community;
    }

    public void setId_community(int id_community) {
        this.id_community = id_community;
    }

    public int getId_user() {
        return id_user;
    }

    public void setId_user(int id_user) {
        this.id_user = id_user;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }
}
