package elcarmen.project.community.Business;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Message {

    private int id;
    private int id_chat;
    private int id_user;
    private String message;
    private boolean seen;
    private Date created_at;
    private boolean sent;

    public Message(int id, int id_chat, int id_user, String message, boolean seen, String created_at) {
        this.id = id;
        this.id_chat = id_chat;
        this.id_user = id_user;
        this.message = message;
        this.seen = seen;

        String dateIn = created_at.split("T")[0];
        String timeIn = created_at.split("T")[1];//.split(".")[0];
        timeIn = timeIn.split("\\.")[0];
        String fechaHora = dateIn + " " + timeIn;
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = new Date();
        try{
            date = format.parse(fechaHora);
        }catch(Exception e){
            e.printStackTrace();
        }
        this.created_at = date;
        this.sent = true;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_chat() {
        return id_chat;
    }

    public void setId_chat(int id_chat) {
        this.id_chat = id_chat;
    }

    public int getId_user() {
        return id_user;
    }

    public void setId_user(int id_user) {
        this.id_user = id_user;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public String getCreated_at_to_String(){
        // Conversión de zona horaria, tomada de: https://code.i-harness.com/es/q/750a53
        Calendar calendar = Calendar.getInstance();
        //calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        calendar.setTime(created_at);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a");

        //Here you say to java the initial timezone. This is the secret
        //sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        //String t1 = sdf.format(calendar.getTime());

        //Here you set to your timezone
        TimeZone timeZone = TimeZone.getDefault();
        sdf.setTimeZone(timeZone);
        //String t2 = sdf.format(calendar.getTime());
        //Will return on your default Timezone
        return sdf.format(calendar.getTime());
    }

    public boolean isSent() {
        return sent;
    }

    public void setSent(boolean sent) {
        this.sent = sent;
    }
}
